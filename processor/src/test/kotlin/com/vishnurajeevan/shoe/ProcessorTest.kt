package com.vishnurajeevan.shoe

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCompilerApi::class)
class ProcessorTest {

  val dataModifier = SourceFile.kotlin(
    "DataModifier.kt", """
      package com.example.test

      interface DataModifier
    """.trimIndent()
  )

  val userRepository = SourceFile.kotlin(
    "UserRepository.kt", """
      package com.example.test

      interface UserRepository
      
    """.trimIndent()
  )

  val model = SourceFile.kotlin(
    "SimpleModel.kt", """
      package com.example.test
      
      data class SimpleModel(
        val name: String
      )
    """.trimIndent()
  )

  val event = SourceFile.kotlin(
    "SimpleEvent.kt", """
      package com.example.test

      sealed interface SimpleEvent {
        data class Save(val name: String): SimpleEvent
      }
    """.trimIndent()
  )
  val defaultSources = listOf(dataModifier, userRepository, model, event)

  @Test
  fun basic() {
    val compilation = KotlinCompilation().apply {
      sources = defaultSources + listOf(
        SourceFile.kotlin(
          "SimplePresenter.kt", """
          package com.example.test

          import androidx.compose.runtime.Composable
          import com.vishnurajeevan.shoe.annotation.ShoeIn
          import kotlinx.coroutines.flow.Flow
      
          @ShoeIn(SimpleEvent::class)
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

    assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
  }

  @Test
  fun basic_noComposable() {

    val compilation = KotlinCompilation().apply {
      sources = defaultSources + listOf(
        SourceFile.kotlin(
          "SimplePresenter.kt", """
          package com.example.test
      
          import com.vishnurajeevan.shoe.annotation.ShoeIn
          import kotlinx.coroutines.flow.Flow
      
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

    assertEquals(result.exitCode, KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertEquals(result.messages.contains("@ShoeIn can only be applied to a composable presenter"), true)
  }

  @Test
  fun modelParameter() {
    val compilation = KotlinCompilation().apply {
      sources = defaultSources + listOf(
        SourceFile.kotlin(
          "SimplePresenter.kt", """
          package com.example.test

          import androidx.compose.runtime.Composable
          import com.vishnurajeevan.shoe.annotation.ShoeIn
          import kotlinx.coroutines.flow.Flow
      
          @ShoeIn(SimpleEvent::class)
          @Composable
          fun SimplePresenter(
              initialModel: SimpleModel = SimpleModel("meow"),
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

    assertEquals(result.exitCode, KotlinCompilation.ExitCode.OK)
  }
}
