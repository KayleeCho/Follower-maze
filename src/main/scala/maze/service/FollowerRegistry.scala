package maze.service

import scala.collection.mutable


class FollowersRegistry {

  private val followRegistry = new mutable.HashMap[Long, Set[Long]]

  def follow(fromUserId: Long, toUserId: Long): Unit = {
    val followers = getFollowersOf(toUserId)
    val newFollowers = followers + fromUserId
    followRegistry.put(toUserId, newFollowers)
  }

  def unfollow(fromUserId: Long, toUserId: Long): Unit = {
    val followers = getFollowersOf(toUserId)
    val newFollowers = followers - fromUserId
    followRegistry.put(toUserId, newFollowers)
  }

  def getFollowersOf(userId: Long): Set[Long] =
    followRegistry.getOrElse(userId, Set.empty)
}