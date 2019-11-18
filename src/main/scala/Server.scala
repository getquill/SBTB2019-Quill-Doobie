import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._
import config.Config
import db.Database
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import service.{PeopleTrollsRobotsService, PeopleTrollsRobotsServiceQuill, SimpleService} // Needed for <+>

object Server extends IOApp  {
  def run(args: List[String]): IO[ExitCode] = {

    val transactorResource =
      for {
        config <- Resource.liftF(Config.load())
        transactor <- Database.create.transactor(config.database)
      } yield (config, transactor)

    transactorResource.use { case (config, transactor) =>
      val server =
        Stream.eval(Database.create.initialize(transactor)).flatMap { _ =>
          val app = Router("/" -> {
            val simpleService = new SimpleService(transactor).service
            val largeService = new PeopleTrollsRobotsService(transactor).service
            val largeQuillService = new PeopleTrollsRobotsServiceQuill(transactor).service
            simpleService <+> largeService <+> largeQuillService
          }).orNotFound // it's a plain Routes object without the .orNotFound
          BlazeServerBuilder[IO]
            .bindHttp(config.server.port, config.server.host)
            .withHttpApp(app)
            .serve
        }

      server.compile.drain.map(_ => ExitCode.Success)
    }
  }
}
