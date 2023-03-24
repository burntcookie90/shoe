package com.vishnurajeevan.shoe

import androidx.compose.runtime.Composable
import com.google.auto.service.AutoService
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.vishnurajeevan.shoe.annotation.ShoeIn
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShoeProcessor(
  val codeGenerator: CodeGenerator,
  val options: Map<String, String>,
  private val logger: KSPLogger
) : SymbolProcessor {
  override fun process(resolver: Resolver): List<KSAnnotated> {
    val annotationName = ShoeIn::class.qualifiedName!!
    val symbols = resolver.getSymbolsWithAnnotation(annotationName)
    val ret = symbols.filterNot { it.validate() }.toList()
    symbols.filter { it is KSFunctionDeclaration && it.validate() }
      .forEach {
        logger.info("Visiting: $it")
        it.accept(ShoeVisitor(), Unit)
      }

    return ret.toList()
  }

  private inner class ShoeVisitor : KSVisitorVoid() {
    val composableClassName = ClassName("androidx.compose.runtime", "Composable")
    private val composableAnnotation = AnnotationSpec
      .builder(composableClassName)
      .build()


    @OptIn(KspExperimental::class)
    override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
      logger.info("visiting function", function)
      if (function.annotations.none { it.toAnnotationSpec() == composableAnnotation }) {
        logger.error(
          "@ShoeIn can only be applied to a composable presenter",
          function
        )
      }

      val shoeIn = function.annotations.filter {
        it.shortName.getShortName() == ShoeIn::class.simpleName
      }.first()
      val returnType = function.returnType!!.toTypeName()
      val eventClass = (shoeIn.arguments.first().value as KSType).toTypeName()
      val parameters = function.parameters
        .map { it.name!!.getShortName() to it.type.toTypeName() }
        .filterNot { (_, type) ->
          ((type is ParameterizedTypeName) && type.typeArguments.first() == eventClass)
              || type == returnType
        }
        .toMap()
      val presenterData = PresenterData(
        packageName = function.packageName.asString(),
        presenterName = function.simpleName.getShortName(),
        className = "${function.simpleName.getShortName()}Factory",
        parameters = parameters,
        returnType = returnType,
        flowType = eventClass
      )
      val fileSpec = FileSpec.builder(
        packageName = presenterData.packageName,
        fileName = presenterData.className
      ).apply {
        val constructorBuilder = FunSpec.constructorBuilder()
          .addAnnotation(Inject::class)

        presenterData.parameters.map { (name, type) ->
          ParameterSpec.builder(name, type)
            .build()
        }.forEach(constructorBuilder::addParameter)

        val properties = presenterData.parameters.map { (name, type) ->
          PropertySpec.builder(name, type)
            .initializer(name)
            .addModifiers(KModifier.PRIVATE)
            .build()
        }

        val constructor = constructorBuilder.build()

        val statement = buildString {
          append("return ${presenterData.presenterName}(\nevents = events,\n")
          presenterData.parameters.forEach { (name, type) ->
            append("$name = %$name:L,\n")
          }
          append(")")
        }

        val params = presenterData.parameters
          .map { (name, _) -> name to name }
          .toMap()
        val codeblock = CodeBlock.builder()
          .addNamed(statement, params)
          .build()

        val modelsFunction = FunSpec.builder("models")
          .addAnnotation(Composable::class)
          .addParameter(ParameterSpec("events", Flow::class.asClassName().parameterizedBy(presenterData.flowType)))
          .returns(presenterData.returnType)
          .addCode(codeblock)
          .build()

        addType(
          TypeSpec.classBuilder(presenterData.className)
            .primaryConstructor(constructor)
            .addProperties(properties)
            .addFunction(modelsFunction)
            .build()
        )
      }.build()
      fileSpec.writeTo(codeGenerator = codeGenerator, aggregating = false)
    }
  }

  data class PresenterData(
    val packageName: String,
    val presenterName: String,
    val className: String,
    val parameters: Map<String, TypeName>,
    val returnType: TypeName,
    val flowType: TypeName,
  )
}

@AutoService(SymbolProcessorProvider::class)
class ShoeProcessorProvider : SymbolProcessorProvider {
  override fun create(environment: SymbolProcessorEnvironment): ShoeProcessor =
    ShoeProcessor(
      codeGenerator = environment.codeGenerator,
      options = environment.options,
      logger = environment.logger
    )

}
