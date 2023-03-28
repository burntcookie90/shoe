# Shoe

A small ksp library to shoe your `@Composable` presenters into a dagger graph.

## Example

```kotlin
@ShoeIn(DogListEvents::class)
@Composable
fun DogListPresenter(
  initialModel: DogListModel,
  events: Flow<DogListEvents>,
  dogRepository: DogRepository
) { ... }

```

will generate the following factory class that you can inject

```kotlin
public class DogListPresenterFactory @Inject constructor(
  private val dogRepository: DogRepository,
) {
  @Composable
  public fun models(events: Flow<DogListEvents>): DogListModel = DogListPresenter(
  events = events,
  dogRepository = dogRepository,
  )
}
```

Now you can use this with your view models (maybe with [MoleculeViewModel](https://github.com/cashapp/molecule/blob/trunk/sample-viewmodel/src/main/java/com/example/molecule/viewmodel/MoleculeViewModel.kt)): 

```kotlin
@HiltViewModel
class DogListViewModel @Inject constructor(private val factory: DogListPresenterFactory) 
  : MoleculeViewModel<DogListEvent, DogListModel>() {

  @Composable
  override fun models(events: Flow<DogListEvent>) = factory.models(events)
}
```


