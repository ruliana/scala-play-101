package controllers

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.util.UUID

import controllers.UserDao.ctx
import io.getquill._
import io.getquill.context.async.SqlTypes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserDao {

  val ctx = new PostgresAsyncContext[SnakeCase]("db.default")

  import ctx._

  def encodeThis[T](sqlType: DecoderSqlType)(f: (T, PrepareRow) => PrepareRow): Encoder[T] =
    AsyncEncoder[T](sqlType)((_, value, row) => f(value, row))

  def decodeThis[T](sqlType: DecoderSqlType)(f: (Index, ResultRow) => T): Decoder[T] =
    AsyncDecoder[T](sqlType)((index, row) => f(index, row))

  implicit val zonedDateTimeEncoder: Encoder[ZonedDateTime] =
    encodeThis(SqlTypes.TIMESTAMP_WITH_TIMEZONE) { (value, row) =>
      row :+ value.format(ISO_OFFSET_DATE_TIME)
    }

  implicit val zonedDateTimeDecoder: Decoder[ZonedDateTime] =
    decodeThis(SqlTypes.TIMESTAMP_WITH_TIMEZONE) { (index, row) =>
      ZonedDateTime.parse(row(index).toString)
    }

  private[this] val users = quote(querySchema[PersistedUser]("users"))

  def insert(user: TransientUser): Future[PersistedUser] = {
    val persistedUser = user.persist(UUID.randomUUID(), ZonedDateTime.now())
    run(users.insert(lift(persistedUser))).map(_ => persistedUser)
  }

  def selectByUuid(uuid: UUID): Future[Option[PersistedUser]] = {
    run(users.filter(_.uuid == lift(uuid))).map(_.headOption)
  }

  def selectAll(): Future[List[PersistedUser]] = {
    run(users.sortBy(_.createdAt)(Ord.descNullsFirst))
  }
}

sealed trait User {
  def name: String
  def email: String
}

case class TransientUser(name: String, email: String) extends User {
  def persist(uuid: UUID, createdAt: ZonedDateTime) = PersistedUser(uuid, name, email, createdAt)
}

case class PersistedUser(uuid: UUID, name: String, email: String, createdAt: ZonedDateTime) extends User
