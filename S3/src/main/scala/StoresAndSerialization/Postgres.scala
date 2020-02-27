package StoresAndSerialization

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import com.typesafe.config.ConfigFactory

object Postgres extends App {
	PGLesson.usingPostgres.simulation

	/**
	  * build.sbt:
	  * // JDBC with PostgreSQL
	  * "org.postgresql" % "postgresql" % postgresVersion,
	  * "com.github.dnvriend" %% "akka-persistence-jdbc" % "3.4.0",
	  *
	  * application.conf:
		postgresDemo{
		    akka.persistence.journal.plugin = "jdbc-journal"
		    akka.persistence.snapshot-store.plugin = "jdbc-snapshot-store"
		    akka-persistence-jdbc{
		        shared-databases{
		            slick{
		                profile ="slick.jdbc.PostgresProfile$"
		                db {
		                    numThreads = 10
		                    driver = "org.postgresql.Driver"
		                    url =
		                    user =
		                    password =
		                }
		            }
		        }
		    }
		}
	  *
		services:
			  postgres:
				    image: postgres:latest
				    container_name: postgres
				    environment:
					      - "TZ=Europe/Amsterdam"
					      - "POSTGRES_USER=docker"
					      - "POSTGRES_PASSWORD=docker"
				    ports:
					      - "5432:5432"
				    volumes:
					      - "./sql:/docker-entrypoint-initdb.d"
	  *
	  *
	  * #create-database.sql
			  --Setup database
		DROP DATABASE IF EXISTS rtjvm;
		CREATE DATABASE rtjvm;
		\c rtjvm;


		-- pretty much standard config for postgres in the context of Akka Persistence
		-- there are almost no things you should change regardless of your message structure

		DROP TABLE IF EXISTS public.journal;

		CREATE TABLE IF NOT EXISTS public.journal (
			  ordering BIGSERIAL,
			  persistence_id VARCHAR(255) NOT NULL,
			  sequence_number BIGINT NOT NULL,
			  deleted BOOLEAN DEFAULT FALSE,
			  tags VARCHAR(255) DEFAULT NULL,
			  message BYTEA NOT NULL,
			  PRIMARY KEY(persistence_id, sequence_number)
		);

		CREATE UNIQUE INDEX journal_ordering_idx ON public.journal(ordering);

		DROP TABLE IF EXISTS public.snapshot;

		CREATE TABLE IF NOT EXISTS public.snapshot (
			  persistence_id VARCHAR(255) NOT NULL,
			  sequence_number BIGINT NOT NULL,
			  created BIGINT NOT NULL,
			  snapshot BYTEA NOT NULL,
			  PRIMARY KEY(persistence_id, sequence_number)
);
	  docker-compose up
	  ./psql.sh
	  */
}

object PGLesson{
	object usingPostgres {
		object simulation{
			import persistenceActor._
			val postgresActorSystem = ActorSystem("postgresSystem", ConfigFactory.load().getConfig("postgresDemo"))
			val actor = postgresActorSystem.actorOf(Props[SimplePeristentActor], "spActor" )
			for (i <- 1 to 10){
				actor ! s"I love Akka [$i]"
			}
			actor ! "print"
			actor ! "snap"

			for(i <- 11 to 20){
				actor ! s"I love Akka [$i]"
			}
		}
		object persistenceActor {
			class SimplePeristentActor extends PersistentActor with ActorLogging{
				override def persistenceId: String = "simple-persistent-actor"

				//mutable state
				var nMessages = 0

				override def receiveCommand: Receive = {
					case "print" =>
						log.info(s"I have persisted $nMessages so far")
					case "snap" =>
						saveSnapshot(nMessages)
					case SaveSnapshotSuccess(metadata) =>
						log.info(s"Snapshot successful; $metadata")
					case SaveSnapshotFailure(_, cause) =>
						log.warning(s"Snapshot failed: $cause")
					case message => persist(message){ _ =>
						log.info(s"Persisting $message")
						nMessages += 1
					}
				}

				override def receiveRecover: Receive = {
					case SnapshotOffer(metadata, payload: Int) =>
						log.info(s"Recovered snapshot: $payload")
						nMessages = payload
					case message =>
						log.info(s"Recovered $message")
						nMessages += 1
					case RecoveryCompleted =>
						log.info("Recovery Done")
				}
			}
		}
	}
}

