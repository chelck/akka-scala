package org.bohdi.mandelbrot

import akka.actor.{Props, Actor, ActorRef}
import akka.routing.{Broadcast, RoundRobinPool}


class Model extends Actor {
  var mandelbrot: List[(Int, Int, Int)] = List()
  var viewPort = ViewPort().center(0.27, 0.0054)
  var pallete: Pallete = new OddEvenPallete
  var job = 1


  def receive = {
    case (environment: Environment, guiActor: ActorRef) =>
      val workers = context.actorOf(RoundRobinPool(environment.workers).props(Props[Worker]), "workerRouter")
      workers ! Broadcast(environment)

      context.become(active(environment, workers, guiActor))
  }


  def active(env: Environment, workers: ActorRef, guiActor: ActorRef): Actor.Receive = {

    case Start =>
      calculate(env, workers)

    case ZoomIn =>
      viewPort = viewPort.zoom(0.7)
      calculate(env, workers)

    case ZoomIn100 =>
      (0 to 20) foreach {n: Int =>
        println("z " + n)
        viewPort = viewPort.zoom(0.7)
        calculate(env, workers)
      }

    case ZoomOut =>
      viewPort = viewPort.zoom(1/0.7)
      calculate(env, workers)

    case PanLeft =>
      viewPort = viewPort.panLeft
      calculate(env, workers)

    case PanRight =>
      viewPort = viewPort.panRight
      calculate(env, workers)

    case PanUp =>
      viewPort = viewPort.panUp
      calculate(env, workers)

    case PanDown =>
      viewPort = viewPort.panDown
      calculate(env, workers)

    case ChangePallete(newPallete) =>
      pallete = newPallete
      calculate(env, workers)


    case Result(job, tile, elements) =>
      if (this.job == job) {
        guiActor ! MandelbrotResult(job, tile, elements)
      }
        //context.stop(self)
      //}

    case x:Any => println(s"Model got junk: $x")
  }

  def calculate(env: Environment, workers: ActorRef) = {
    job = job + 1

    workers ! Broadcast(job)

    for (tile <- env.tiles) {
      workers ! Work(job, tile, viewPort, pallete)
    }
  }
}
