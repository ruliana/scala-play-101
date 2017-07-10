package controllers

import java.util.UUID
import javax.inject._

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class HomeController @Inject()(components: MessagesControllerComponents)
  extends MessagesAbstractController(components) {

  val transientUserForm: Form[TransientUser] = Form(
    mapping(
      "name" -> nonEmptyText(minLength = 3, maxLength = 255),
      "email" -> nonEmptyText(minLength = 5, maxLength = 255)
    )(TransientUser.apply)(TransientUser.unapply)
  )

  def index = Action.async { implicit request: Request[AnyContent] =>
    UserDao.selectAll().map { users => Ok(views.html.index(users)) }
  }

  def registerForm = Action { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.registerForm(transientUserForm))
  }

  def register = Action.async { implicit request: MessagesRequest[AnyContent] =>
    transientUserForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.registerForm(formWithErrors)))
      },
      user => {
        UserDao.insert(user) map { persistedUser =>
            Redirect(routes.HomeController.registerSuccess(persistedUser.uuid))
        } recover {
          // TODO: add log
          case ex: Exception => InternalServerError(ex.getLocalizedMessage)
        }
      }
    )
  }

  def registerSuccess(uuid: UUID) = Action.async { implicit request: Request[AnyContent] =>
    UserDao.selectByUuid(uuid) map {
      case Some(user) => Ok(views.html.registerSuccess(user))
      case None => NotFound
    }
  }
}