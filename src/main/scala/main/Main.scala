package main

import dto.{Group, Movie, Order}
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import scalikejdbc._
import scalikejdbc.config._
import util.JsonUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Random

object Main {

  def main(args: Array[String]): Unit = {
    DBs.setupAll()
    consumeMovies()
    println(selectMovies())
    purchase(9, generateOrderId(), "Adik", 1, 200)
  }

  def generateOrderId(length: Int = 6) = {
    Random.alphanumeric.take(length).mkString("")
  }
  def consumeMovies(): Unit = {
    val url = "https://api.themoviedb.org/3/discover/movie?api_key=831dca13428df477bc3d159e180930d3&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1"
    val client = HttpClients.createDefault()
    val getFlowInfo: HttpGet = new HttpGet(url)

    val response: Option[CloseableHttpResponse] = {
      try {
        Some(client.execute(getFlowInfo))
      } catch {
        case e: Exception =>
          None
      }
    }
    if (!response.isEmpty) {
      insertMovie(response)
      println("List Movies Saved")
    } else {
      println("List Movies Failed To save,check your Connection")
    }
  }

  def selectMovies(): List[Movie] = {
    DB.readOnly { implicit session =>
      SQL("select * from movie")
        .map(rs => Movie(rs.boolean("adult"), rs.string("backdrop_path"), rs.int("id"), rs.string("original_language"), rs.string("original_title"), rs.string("overview"), rs.double("popularity"), rs.string("poster_path"), rs.string("release_date"), rs.string("title"), rs.boolean("video"), rs.int("vote_average"), rs.int("vote_count")))
        .list().apply()
    }
  }

  def insertMovie(response: Option[CloseableHttpResponse]): Unit = {
    val entity = response.get.getEntity
    val str = EntityUtils.toString(entity, "UTF-8")
    val group = JsonUtil.fromJson[Group](str)
    DB.localTx { implicit session =>
      for (m <- group.results) {
        SQL("insert into movie(adult, backdrop_path,original_language, original_title, overview, popularity, poster_path, release_date, title, video, vote_average, vote_count) values(?,?,?,?,?,?,?,?,?,?,?,?)")
          .bind(m.adult, m.backdrop_path, m.original_language, m.original_title, "-", m.popularity, m.poster_path, m.release_date, m.title, m.video, m.vote_average, m.vote_count)
          .update().apply()
      }
    }
  }

  def findMovieById(id: Int): Future[Movie] = Future {
    var movie: Option[Movie] = DB readOnly { implicit session =>
      sql"select * from movie where id = ${id}"
        .map(rs => Movie(rs.boolean("adult"), rs.string("backdrop_path"), rs.int("id"), rs.string("original_language"), rs.string("original_title"), rs.string("overview"), rs.double("popularity"), rs.string("poster_path"), rs.string("release_date"), rs.string("title"), rs.boolean("video"), rs.int("vote_average"), rs.int("vote_count"))).single().apply()
    }
    var m = movie.get
    Movie(m.adult, m.backdrop_path,m.id, m.original_language, m.original_title, "-", m.popularity, m.poster_path, m.release_date, m.title, m.video, m.vote_average, m.vote_count)
  }

  def createTransaction(movie: Movie, orderId: String, username: String, qty: Int, price: Double): Future[Order] = Future {
    DB.localTx { implicit session =>
      SQL("insert into movie_order(id,username,price,qty,movie_id) values(?,?,?,?,?)")
        .bind(orderId,username,price,qty,movie.id)
        .update().apply()
    }
    Order(orderId, username, price, qty, movie.id)
  }

  def purchase(movie_id: Int, orderId: String, username: String, qty: Int, price: Double): Unit = {
    // fetch the user from the DB
    // create a transaction
    // WAIT for the transaction to finish
    val transactionFuture = for {
      movie <- findMovieById(movie_id)
      transaction <- createTransaction(movie, orderId, username, qty, price)
    } yield transaction

    Await.result(transactionFuture, 2.seconds)
    println("purchase Complete")
  }

}


