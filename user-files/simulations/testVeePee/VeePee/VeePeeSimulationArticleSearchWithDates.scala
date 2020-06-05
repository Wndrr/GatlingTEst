package VeePeeSimulation

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class VeePeeSimulationArticleSearchWithDates extends Simulation {
	val httpProtocol = http
		.baseUrl("https://ctv-veepee.azurewebsites.net")
		//.baseUrl("http://localhost:5558/")
		.inferHtmlResources(BlackList(), WhiteList("ArticlePageRequest", "ArticlePageRequestCustom", "EstablishmentPage"))
		.acceptHeader("application/json, text/javascript, */*; q=0.01")
		.acceptEncodingHeader("gzip, deflate")

        
	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"Accept-Language" -> "en-US,en;q=0.9,fr;q=0.8,de;q=0.7",
		"Sec-Fetch-Dest" -> "document",
		"Sec-Fetch-Mode" -> "navigate",
		"Sec-Fetch-Site" -> "none",
		"Sec-Fetch-User" -> "?1",
		"Upgrade-Insecure-Requests" -> "1",
		"User-Agent" -> "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")

	val feeder = csv("VeePeeSimulationArticleSearchWithDates_Data.csv").random 

		val scn = scenario("VeePeeSimulationArticleSearch").exec(
			http("ArticlePageRequest")
			.get("/camping-paradis?campaignId=dfsd&RequestId=gffgggff")
			.headers(headers_0)
			.check(status.is(200))	
		)
		.pause(1)
		.feed(feeder)
		.exec(
			http("ArticlePageRequestCustom")
			.get("/camping-paradis?pageNumber=1&SortOrder=Descending&SortType=Original&ChainePersonsDetails=${ChainePersonsDetails}&BeginDate=${BeginDate}&EndDate=${EndDate}&Radius=20&RequestId=gffgggff&CampaignId=dfsd&IsCustomData=true&IsFormFirstSend=true&id=10125")
			.headers(headers_0)
			.check(status.is(200))
			.check(css(".top-infos:first > a", "href").saveAs("firstEstablishmenturl"))
		)
		.exitHereIfFailed
		.pause(1)
		.exec(
			http("EstablishmentPage")
			.get("${firstEstablishmenturl}")
			.headers(headers_0)
			.check(status.is(200))			
		)
	

	setUp(scn.inject(constantUsersPerSec(10) during (60 minutes))).protocols(httpProtocol)
}