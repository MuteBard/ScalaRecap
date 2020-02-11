package FaultTolerance

object Supervision extends App{
	/*
	It's fine if actors crash
	Parents must decide upon their children's failure

	when an actor fails, it:
		+ suspends its children
		+ sends a special message to its parent

	parent can decide to:
	    + resume the actor
	    + restart the actor and clears its internal state and stops all its children
	    + stop the actor and stops all its children
	    + escalate and fail itse
	 */

}
