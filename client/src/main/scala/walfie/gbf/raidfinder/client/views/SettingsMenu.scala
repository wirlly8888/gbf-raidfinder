package walfie.gbf.raidfinder.client.views

import com.thoughtworks.binding
import com.thoughtworks.binding.Binding
import com.thoughtworks.binding.Binding._
import org.scalajs.dom
import org.scalajs.dom.raw._
import scala.scalajs.js
import walfie.gbf.raidfinder.BuildInfo
import walfie.gbf.raidfinder.client._
import walfie.gbf.raidfinder.client.syntax.StringOps
import walfie.gbf.raidfinder.client.ViewModel._
import walfie.gbf.raidfinder.client.ViewModel.ImageQuality._
import walfie.gbf.raidfinder.protocol._

object SettingsMenu {
  @binding.dom
  def content(client: RaidFinderClient, viewState: State): Binding[Element] = {
    val onChange = () => ViewModel.persistState(viewState)

    <section id="gbfrf-dialog__settings" class={
      val isActive = viewState.currentTab.bind == DialogTab.Settings
      "gbfrf-dialog__content mdl-layout__tab-panel".addIf(isActive, "is-active")
    }>
      <div class="gbfrf-settings__content">
        <ul class="mdl-list" style="padding: 0; margin: 0;">
          {
            settingsListItem("Time format") {
              radioSelector(TimeFormat.all, viewState.timeFormat, onChange)
            }.bind
          }
          {
            settingsListItem("Boss image quality") {
              radioSelector(ImageQuality.all, viewState.imageQuality, onChange)
            }.bind
          }
          {
            settingsListItem("Show user images") {
              checkboxAction("gbfrf-setting__user-image", viewState.showUserImages, onChange)
            }.bind
          }
        </ul>
        { footer.bind }
      </div>
    </section>
  }

  @binding.dom
  def footer: Binding[HTMLDivElement] = {
    // TODO: Don't hardcode this
    <div class="gbfrf-settings__footer">
      <span>{ "v" + BuildInfo.version }</span><br/>
      <a href="https://github.com/walfie/gbf-raidfinder" target="_blank">
        github.com/walfie/gbf-raidfinder
      </a>
    </div>
  }

  @binding.dom
  def settingsListItem(text: String)(secondaryAction: Binding[Element]): Binding[HTMLLIElement] = {
    <li class="gbfrf-settings__item mdl-list__item">
      <div class="mdl-list__item-primary-content">{ text }</div>
      { secondaryAction.bind }
    </li>
  }

  @binding.dom
  def checkboxAction(id: String, checked: Var[Boolean], onChange: () => Unit): Binding[HTMLDivElement] = {
    <div class="mdl-list__item-secondary-action">
      { checkbox(id, checked, onChange).bind }
    </div>
  }

  @binding.dom
  def checkbox(id: String, checked: Var[Boolean], onChange: () => Unit): Binding[HTMLLabelElement] = {
    val onClick = { e: dom.Event =>
      checked := e.currentTarget.asInstanceOf[HTMLInputElement].checked
      onChange()
    }

    val input = <input type="checkbox" id={ id } class="mdl-switch__input" onclick={ onClick }/>
    input.checked = checked.get

    <label class="mdl-switch mdl-js-switch mdl-js-ripple-effect" for={ id }>
      { input }
    </label>
  }

  @binding.dom
  def radioSelector[T <: Labeled](
    allValues:    Seq[T],
    currentValue: Var[T],
    onChange:     () => Unit
  ): Binding[HTMLDivElement] = {
    <div class="gbfrf-settings__radio-buttons">
      {
        Constants(allValues: _*).map { value =>
          val onClick = { e: Event =>
            currentValue := value
            onChange()
          }
          val radioInput =
            <input type="radio" id={ value.id } class="mdl-radio__button" value={ value.label } onclick={ onClick }/>

          val label =
            <label class="mdl-radio mdl-js-radio mdl-js-ripple-effect" for={ value.id }>
              {
                radioInput.checked = (currentValue.bind == value)
                radioInput
              }
              <span class="mdl-radio__label">{ value.label }</span>
            </label>

          label
        }
      }
    </div>
  }

}

