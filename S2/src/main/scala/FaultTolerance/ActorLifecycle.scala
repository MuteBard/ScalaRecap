package FaultTolerance

object ActorLifecycle extends App{
	/*
	Actor instance
		- has methods
		- may have internal state
		- Remember that this was demoted by a diamond shape

	Actor reference aka incarnation
		- created with actorOf
		- Remember that this was demoted by a circle with the [queue]
		- the only way you can communicate with an actor is via this actor referernce
		- contains a Unique identifier UUID given by the actor system

	Actor Path
		- a space in the actor system which may or may not be occupied with an actor reference
		- think of it as a yellow rounded rectangle that surrounds everything



	Actors can be
		- started : create a new ActorRef with a UUID at a given path
		- restarted :
			+ suspended
			+ swap the actor instance:
				* old instance calls preRestart
				* replace actor instance
				* internal state is destroyed on restart
				* new instance cals postRestart, taking the place of the old instance
			+ resume
		- resumed : the ActorRef will continue processing more messages
		- suspended : the ActorRef will enqueue but  not process more messages
		- stopped :
			+ frees the actor ref within a path
			+ calls postStop
			+ all watching actors receive Terminated(ref)

	 */

}
