package paperstx.components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^._
import paperstx.builder.BuildValidation

import scalacss.ScalaCssReact._

object MainComponent {
  case class State(selectedLangSrc: Option[BuildValidation[String]])

  class Backend(scope: BackendScope[Unit, State]) {
    def render(state: State): VdomElement = {
      <.div(paperstx.Styles.app, HeaderComponent(onLangSelect = { newLangSrc =>
        scope.setState(State(newLangSrc))
      }), ContentComponent(state.selectedLangSrc))
    }
  }

  object State {
    val empty: State = State(selectedLangSrc = None)
  }

  val component =
    ScalaComponent
      .builder[Unit]("Main")
      .initialState(State.empty)
      .renderBackend[Backend]
      .build

  def apply(): VdomElement = component()
}
