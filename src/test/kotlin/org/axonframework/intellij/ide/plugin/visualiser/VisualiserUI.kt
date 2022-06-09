package org.axonframework.intellij.ide.plugin.visualiser

import com.google.gson.Gson
import java.awt.FlowLayout
import java.io.FileReader
import javax.swing.JButton
import javax.swing.JFrame
import org.axonframework.intellij.ide.plugin.visualiser.ui.EventModelDialogWrapper

fun main(args: Array<String>) {
  // val structure = Gson().fromJson(structureJson, AxonProjectModel::class.java)
  val structure = Gson().fromJson(FileReader("complex-model.json"), AxonProjectModel::class.java)
  //  val model1 =
  //  // EventModelBuilder(structure).build("uk.co.skipoles.clashcat.sagas.RegisterClanCommand")
  //  EventModelBuilder(structure).build("uk.co.skipoles.clashcat.sagas.RegisterClanCommand",
  // listOf())
  //  val model2 = handCraftedModel()
  //  println(model1)
  //
  //  val visualisation = EventModelScrollPane()
  //
  val frame = JFrame("Test")
  frame.contentPane.layout = FlowLayout()
  //    frame.contentPane.add(visualisation)
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

  val button = JButton("Start...")
  button.addActionListener { EventModelDialogWrapper(structure).show() }
  frame.add(button)
  //    frame.size = Dimension(1600, 1000)
  frame.pack()
  frame.isVisible = true
  //
  //  Thread.sleep(2000)
  //  visualisation.visualise(model1)
  //
  //  Thread.sleep(5000)
  //  visualisation.clear()

}

private fun handCraftedModel(): AxonEventModel {
  val command1PostIt =
      CommandPostIt(
          Command(
              "Command 1",
              createdBy = CommandCreatorDetail(setOf()),
              handledBy =
                  CommandHandlerDetail(
                      HandlerType.CommandHandler,
                      "Handler:",
                      events = setOf(),
                      commands = setOf())),
          SwimLane(SwimLaneType.Timeline, 1),
          columnIndex = 4)

  val command2PostIt =
      CommandPostIt(
          Command(
              "Command 2",
              createdBy = CommandCreatorDetail(setOf()),
              handledBy =
                  CommandHandlerDetail(
                      HandlerType.CommandHandler,
                      "Handler:",
                      events = setOf(),
                      commands = setOf())),
          SwimLane(SwimLaneType.Timeline, 1),
          columnIndex = 7)

  val viewPostIt = ViewPostIt("View", SwimLane(SwimLaneType.Timeline, 1), columnIndex = 5)

  val event1PostIt =
      EventPostIt(
          Event("Event 1", createdBy = EventCreatorDetail(setOf()), handledBy = setOf()),
          SwimLane(SwimLaneType.Events, 2),
          columnIndex = 4)

  val event2PostIt =
      EventPostIt(
          Event("Event 2", createdBy = EventCreatorDetail(setOf()), handledBy = setOf()),
          SwimLane(SwimLaneType.Events, 2),
          columnIndex = 1)

  val event3PostIt =
      EventPostIt(
          Event("Event 3", createdBy = EventCreatorDetail(setOf()), handledBy = setOf()),
          SwimLane(SwimLaneType.Events, 2),
          columnIndex = 6)

  val event4PostIt =
      EventPostIt(
          Event("Event 4", createdBy = EventCreatorDetail(setOf()), handledBy = setOf()),
          SwimLane(SwimLaneType.Events, 3),
          columnIndex = 2)

  val event5PostIt =
      EventPostIt(
          Event("Event 5", createdBy = EventCreatorDetail(setOf()), handledBy = setOf()),
          SwimLane(SwimLaneType.Events, 3),
          columnIndex = 7)

  return AxonEventModel(
      "Command 1",
      listOf(
          command1PostIt,
          command2PostIt,
          viewPostIt,
          event1PostIt,
          event2PostIt,
          event3PostIt,
          event4PostIt,
          event5PostIt),
      mapOf(
          //          Pair(event1PostIt, listOf(command1PostIt)),
          //          Pair(event2PostIt, listOf(command1PostIt)),
          //          Pair(event3PostIt, listOf(command1PostIt)),
          //          Pair(event4PostIt, listOf(command1PostIt, command2PostIt)),
          //          Pair(event5PostIt, listOf(command1PostIt, command2PostIt)),
          //          Pair(command1PostIt, listOf(event1PostIt, event2PostIt, event3PostIt,
          // event4PostIt)),
          //          // Pair(command2PostIt, listOf(command1PostIt)),
          //          Pair(viewPostIt, listOf(event1PostIt, event2PostIt, event3PostIt,
          // event4PostIt))))
          ))
}

private val structureJson =
    """
    {
      "commands": [
        {
          "name": "uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand",
          "shortName": "Update From Clan Data",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga"
            ]
          },
          "handledBy": {
            "type": "Aggregate",
            "name": "uk.co.skipoles.clashcat.clan.Clan",
            "shortName": "Clan",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.clan.MemberLeftEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.clan.MemberDetailsChangedEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.clan.ClanUpdatedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.TrackClanCommand",
          "shortName": "Track Clan",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga"
            ]
          },
          "handledBy": {
            "type": "Aggregate",
            "name": "uk.co.skipoles.clashcat.clan.Clan",
            "shortName": "Clan",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.clan.ClanRegisteredEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.player.TrackPlayerCommand",
          "shortName": "Track Player",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga"
            ]
          },
          "handledBy": {
            "type": "Aggregate",
            "name": "uk.co.skipoles.clashcat.player.Player",
            "shortName": "Player",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerTrackedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.sagas.RegisterClanCommand",
          "shortName": "Register Clan",
          "createdBy": {
            "names": []
          },
          "handledBy": {
            "type": "CommandHandler",
            "name": "uk.co.skipoles.clashcat.sagas.SagasCommandHandler",
            "shortName": "Sagas Command Handler",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationReceivedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.sagas.UpdatePlayerCommand",
          "shortName": "Update Player",
          "createdBy": {
            "names": []
          },
          "handledBy": {
            "type": "CommandHandler",
            "name": "uk.co.skipoles.clashcat.sagas.SagasCommandHandler",
            "shortName": "Sagas Command Handler",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateRequestedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand",
          "shortName": "Obtain Player Data From Co C Api",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga"
            ]
          },
          "handledBy": {
            "type": "CommandHandler",
            "name": "uk.co.skipoles.clashcat.clashapi.ClashOfClansService",
            "shortName": "Clash Of Clans Service",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.sagas.UpdateClanCommand",
          "shortName": "Update Clan",
          "createdBy": {
            "names": []
          },
          "handledBy": {
            "type": "CommandHandler",
            "name": "uk.co.skipoles.clashcat.sagas.SagasCommandHandler",
            "shortName": "Sagas Command Handler",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateRequestedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand",
          "shortName": "Stop Tracking Player",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.PlayerTrackingService"
            ]
          },
          "handledBy": {
            "type": "Aggregate",
            "name": "uk.co.skipoles.clashcat.player.Player",
            "shortName": "Player",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.player.StoppedTrackingPlayerEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.DoesClanExistCommand",
          "shortName": "Does Clan Exist",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga"
            ]
          },
          "handledBy": {
            "type": "CommandHandler",
            "name": "uk.co.skipoles.clashcat.clashapi.ClashOfClansService",
            "shortName": "Clash Of Clans Service",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.clashapi.ClanLookupCompletedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand",
          "shortName": "Update From Player Data",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga"
            ]
          },
          "handledBy": {
            "type": "Aggregate",
            "name": "uk.co.skipoles.clashcat.player.Player",
            "shortName": "Player",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerLastActiveEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerChangedNameEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerActivityChangedEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerDonationsChangedEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerDonationsSeasonCompleteEvent"
              },
              {
                "name": "uk.co.skipoles.clashcat.player.PlayerUpdatedEvent"
              }
            ],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand",
          "shortName": "Obtain Clan Data From Co C Api",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga"
            ]
          },
          "handledBy": {
            "type": "CommandHandler",
            "name": "uk.co.skipoles.clashcat.clashapi.ClashOfClansService",
            "shortName": "Clash Of Clans Service",
            "events": [
              {
                "name": "uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent"
              }
            ],
            "commands": []
          }
        }
      ],
      "events": [
        {
          "name": "uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent",
          "shortName": "New Member Joined",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.TrackPlayerCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent",
          "shortName": "Player Changed War Preference",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.StoppedTrackingPlayerEvent",
          "shortName": "Stopped Tracking Player",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.MemberLeftEvent",
          "shortName": "Member Left",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerTrackingService",
              "shortName": "Player Tracking Service",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand"
                }
              ]
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent",
          "shortName": "Player Changed War Preference",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent",
          "shortName": "Player Metrics Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerLastActiveEvent",
          "shortName": "Player Last Active",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.MemberLeftEvent",
          "shortName": "Member Left",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerTrackingService",
              "shortName": "Player Tracking Service",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand"
                }
              ]
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent",
          "shortName": "Clan Information Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanSummaryProjection",
              "shortName": "Clan Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerTrackedEvent",
          "shortName": "Player Tracked",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.MemberDetailsChangedEvent",
          "shortName": "Member Details Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerChangedNameEvent",
          "shortName": "Player Changed Name",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerLastActiveEvent",
          "shortName": "Player Last Active",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerActivityChangedEvent",
          "shortName": "Player Activity Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerChangedNameEvent",
          "shortName": "Player Changed Name",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent",
          "shortName": "New Member Joined",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.TrackPlayerCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerDonationsChangedEvent",
          "shortName": "Player Donations Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent",
          "shortName": "Clan Information Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanSummaryProjection",
              "shortName": "Clan Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent",
          "shortName": "Player Metrics Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.MemberDetailsChangedEvent",
          "shortName": "Member Details Changed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerTrackedEvent",
          "shortName": "Player Tracked",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent",
          "shortName": "Player Changed War Preference",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerDonationsSeasonCompleteEvent",
          "shortName": "Player Donations Season Complete",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.StoppedTrackingPlayerEvent",
          "shortName": "Stopped Tracking Player",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.MemberLeftEvent",
          "shortName": "Member Left",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerTrackingService",
              "shortName": "Player Tracking Service",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand"
                }
              ]
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanRegisteredEvent",
          "shortName": "Clan Registered",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanUpdatedEvent",
          "shortName": "Clan Updated",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "shortName": "Clan Update Saga",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateRequestedEvent",
          "shortName": "Clan Update Requested",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.SagasCommandHandler"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "shortName": "Clan Update Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent",
          "shortName": "Clan Data Obtained",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clashapi.ClashOfClansService"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "shortName": "Clan Update Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand"
                }
              ]
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerUpdatedEvent",
          "shortName": "Player Updated",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "shortName": "Player Update Saga",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerTrackedEvent",
          "shortName": "Player Tracked",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
              "shortName": "Player Summary Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.player.Player",
              "shortName": "Player",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanUpdatedEvent",
          "shortName": "Clan Updated",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "shortName": "Clan Update Saga",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanRegisteredEvent",
          "shortName": "Clan Registered",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateRequestedEvent",
          "shortName": "Player Update Requested",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.SagasCommandHandler"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "shortName": "Player Update Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.ClanLookupCompletedEvent",
          "shortName": "Clan Lookup Completed",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clashapi.ClashOfClansService"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clan.TrackClanCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent",
          "shortName": "New Member Joined",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clan.Clan"
            ]
          },
          "handledBy": [
            {
              "type": "EventHandler",
              "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
              "shortName": "Clan Member Projection",
              "events": [],
              "commands": []
            },
            {
              "type": "AggregateEventSource",
              "name": "uk.co.skipoles.clashcat.clan.Clan",
              "shortName": "Clan",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.TrackPlayerCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent",
          "shortName": "Clan Data Obtained",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clashapi.ClashOfClansService"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
              "shortName": "Clan Update Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand"
                }
              ]
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationReceivedEvent",
          "shortName": "Clan Registration Received",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.sagas.SagasCommandHandler"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
              "shortName": "Clan Registration Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.clashapi.DoesClanExistCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerUpdatedEvent",
          "shortName": "Player Updated",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.player.Player"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": []
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "shortName": "Player Update Saga",
              "events": [],
              "commands": []
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent",
          "shortName": "Player Data Obtained",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clashapi.ClashOfClansService"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "shortName": "Player Update Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand"
                }
              ]
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand"
                }
              ]
            }
          ]
        },
        {
          "name": "uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent",
          "shortName": "Player Data Obtained",
          "createdBy": {
            "names": [
              "uk.co.skipoles.clashcat.clashapi.ClashOfClansService"
            ]
          },
          "handledBy": [
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
              "shortName": "Player Update Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand"
                }
              ]
            },
            {
              "type": "Saga",
              "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
              "shortName": "New Member Joined Saga",
              "events": [],
              "commands": [
                {
                  "name": "uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand"
                }
              ]
            }
          ]
        }
      ],
      "queries": [
        {
          "name": "uk.co.skipoles.clashcat.player.PlayerSummaryQuery",
          "shortName": "Player Summary",
          "handledBy": {
            "type": "QueryHandler",
            "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
            "events": [],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanSummaryQuery",
          "shortName": "Clan Summary",
          "handledBy": {
            "type": "QueryHandler",
            "name": "uk.co.skipoles.clashcat.clan.ClanSummaryProjection",
            "events": [],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClansListQuery",
          "shortName": "Clans List",
          "handledBy": {
            "type": "QueryHandler",
            "name": "uk.co.skipoles.clashcat.clan.ClanSummaryProjection",
            "events": [],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.clan.ClanMembersQuery",
          "shortName": "Clan Members",
          "handledBy": {
            "type": "QueryHandler",
            "name": "uk.co.skipoles.clashcat.clan.ClanMemberProjection",
            "events": [],
            "commands": []
          }
        },
        {
          "name": "uk.co.skipoles.clashcat.player.TrackedPlayersQuery",
          "shortName": "Tracked Players",
          "handledBy": {
            "type": "QueryHandler",
            "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
            "events": [],
            "commands": []
          }
        }
      ]
    }
""".trimIndent()
