package maze.service

trait FollowService {
  val followRegistry = new scala.collection.concurrent.TrieMap[Long, Set[Long]]


  def follow(toUserId: Long, fromUserId: Long) =
    followRegistry.get(toUserId) match {
      case None => followRegistry.put(toUserId, Set(fromUserId)).isDefined
      case Some(followers) =>  followRegistry.replace(toUserId, followers, followers + fromUserId )
    }


  def unfollow(toUserId: Long, fromUserId: Long) =
    followRegistry.get(toUserId) match {
      case None => true
      case Some(followers) =>  followRegistry.replace(toUserId, followers, followers.-(fromUserId))
    }

  def statusUpdate(fromUserId: Long, payload: String): Boolean
}
