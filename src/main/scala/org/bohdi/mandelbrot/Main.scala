package org.bohdi.mandelbrot

import akka.actor.{Props, ActorSystem}
import akka.routing.{Broadcast, RoundRobinPool}


object Main extends App {
  val environment = Environment(1000, 800, 1000, 1, 4)

  val system = ActorSystem("MandelbrotSystem")

  val display = new Display(environment, new CyclePallete)

  val resultHandler = system.actorOf(Props[ResultHandler], "resultHandler")
  val workers = system.actorOf(RoundRobinPool(environment.workers).props(Props[Worker]), "workerRouter")
  val master = system.actorOf(Props[Master], "master")
  val frameActor = system.actorOf(Props[FrameActor].withDispatcher("swing-dispatcher"), "frame-actor")

  resultHandler ! display
  workers ! Broadcast(environment)
  master ! MasterInit(environment, workers, resultHandler)

  master ! Calculate
}
