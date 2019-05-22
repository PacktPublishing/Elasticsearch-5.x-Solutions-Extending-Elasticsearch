package com.packtpub

import com.sksamuel.elastic4s.{ElasticClient, ElasticsearchClientUri}
import com.sksamuel.elastic4s.ElasticDsl._

object ClientSample extends App {
  val uri = ElasticsearchClientUri("elasticsearch://127.0.0.1:9300?cluster.name=elasticsearch")
  val client = ElasticClient.transport(uri)

  // await is a helper method to make this operation synchronous instead of async
  // You would normally avoid doing this in a real program as it will block your thread
  client.execute { indexInto("bands" / "artists") fields "name"->"coldplay" }.await

  // we need to wait until the index operation has been flushed by the server.
  // this is an important point - when the index future completes, that doesn't mean that the doc
  // is necessarily searchable. It simply means the server has processed your request and the doc is
  // queued to be flushed to the indexes. Elasticsearch is eventually consistent.
  // For this demo, we'll simply wait for 2 seconds (default refresh interval is 1 second).
  Thread.sleep(2000)

  // now we can search for the document we indexed earlier
  val resp = client.execute { search("bands" / "artists") query "coldplay" }.await
  println(resp)


}
