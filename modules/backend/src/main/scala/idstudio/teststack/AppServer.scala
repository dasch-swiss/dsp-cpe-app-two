package swiss.dasch.cpe

import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server}
import zio._
import scala.util.Try
import swiss.dasch.cpe.frontend.FrontendApp
import swiss.dasch.cpe.suggestions.SuggestionsApp

object AppServer extends ZIOAppDefault {

  // Set a port
  private val PORT = 8090

  private val server =
    Server.bind("0.0.0.0", PORT) ++               // Setup port
      Server.paranoidLeakDetection ++                    // Paranoid leak detection (affects performance)
      Server.app(FrontendApp(false) ++ SuggestionsApp()) // Setup the Http app

  val run = ZIOAppArgs.getArgs.flatMap { args =>
    // Configure thread count using CLI
    val nThreads: Int = args.headOption.flatMap(x => Try(x.toInt).toOption).getOrElse(0)

    // Create a new server
    server.make
      .flatMap(start =>
        // Waiting for the server to start
        Console.printLine(s"Server started on port ${start.port}")

        // Ensures the server doesn't die after printing
          *> ZIO.never,
      )
      .provide(ServerChannelFactory.auto, EventLoopGroup.auto(nThreads), Scope.default, SuggestionsService.layer)
  }
}
