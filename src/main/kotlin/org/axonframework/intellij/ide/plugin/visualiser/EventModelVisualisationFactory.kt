package org.axonframework.intellij.ide.plugin.visualiser

import com.google.gson.Gson
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane

class EventModelVisualisationFactory(model: AxonEventModel) {

  private val maxWidth: Int = 1500
  private val maxHeight: Int = 900
  private val image = EventModelImageRenderer(model).renderImage()

  fun createVisualisation(): JScrollPane {
    val panel = ImagePanel(image)

    val paneSize = Dimension(minOf(maxWidth, image.width), minOf(maxHeight, image.height))
    val pane = JScrollPane(panel)
    // val pane = JBScrollPane(panel)
    pane.preferredSize = paneSize
    pane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    pane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS

    return pane
  }

  class ImagePanel(private val image: BufferedImage) : JPanel(true) {
    init {
      preferredSize = Dimension(image.width, image.height)
    }

    override fun paintComponent(g: Graphics?) {
      super.paintComponent(g)
      g?.drawImage(image, 0, 0, null)
    }
  }
}

fun main(args: Array<String>) {
  val structure = Gson().fromJson(structureJson, AxonProjectModel::class.java)
  val model =
      EventModelBuilder(structure).build("uk.co.skipoles.clashcat.sagas.RegisterClanCommand")

  val visualisationFactory = EventModelVisualisationFactory(model)
  val frame = JFrame("Test")
  frame.contentPane.layout = FlowLayout()
  frame.contentPane.add(visualisationFactory.createVisualisation())
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
  frame.size = Dimension(1600, 1000)
  frame.isVisible = true
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
