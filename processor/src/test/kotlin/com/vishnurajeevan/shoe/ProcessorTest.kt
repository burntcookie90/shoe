package com.vishnurajeevan.shoe

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
class ProcessorTest {

  @Test
  fun basic() {

    val compilation = KotlinCompilation().apply {
      sources = listOf(
        SourceFile.kotlin(
          "SimplePresenter.kt", """
          package com.exmaple.test
          import androidx.compose.runtime.Composable
          import com.vishnurajeevan.shoe.annotation.ShoeIn
          import kotlinx.coroutines.flow.Flow
      
          interface DataModifier
          interface UserRepository
      
          data class SimpleModel(val name: String)
          sealed interface SimpleEvent {
            data class Save(val name: String): SimpleEvent
          }
      
          @ShoeIn(eventClass = SimpleEvent::class)
          @Composable
          fun SimplePresenter(
              events: Flow<SimpleEvent>,
              modifier: DataModifier,
              repo: UserRepository
          ): SimpleModel {
            return SimpleModel("john")
          }
        """.trimIndent()
        )
      )
      symbolProcessorProviders = listOf(ShoeProcessorProvider())
      inheritClassPath = true
    }

    val result = compilation.compile()

    println(result.exitCode)
  }

  @Test
  fun basic_noComposable() {

    val compilation = KotlinCompilation().apply {
      sources = listOf(
        SourceFile.kotlin(
          "SimplePresenter.kt", """
          package test
      
          import com.vishnurajeevan.shoe.annotation.ShoeIn
          import kotlinx.coroutines.flow.Flow
      
          interface DataModifier
          interface UserRepository
      
          data class SimpleModel(val name: String)
      
          sealed interface SimpleEvent {
            data class Save(val name: String): SimpleEvent
          }
      
          @ShoeIn(SimpleEvent::class)
          fun SimplePresenter(
              events: Flow<SimpleEvent>,
              modifier: DataModifier,
              repo: UserRepository
          ): SimpleModel {
            return SimpleModel("john")
          }
        """.trimIndent()
        )
      )
      symbolProcessorProviders = listOf(ShoeProcessorProvider())
      inheritClassPath = true
    }

    val result = compilation.compile()

    println(result.exitCode)
  }
}
