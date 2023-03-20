package com.vishnurajeevan.shoe

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

fun main() {
  println(SimpleEvent.Save("johnnie").name)
}
