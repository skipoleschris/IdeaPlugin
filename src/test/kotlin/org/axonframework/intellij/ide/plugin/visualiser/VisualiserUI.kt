package org.axonframework.intellij.ide.plugin.visualiser

import com.google.gson.Gson
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JFrame
import org.axonframework.intellij.ide.plugin.visualiser.ui.EventModelDialogWrapper

fun main(args: Array<String>) {
  val structure = Gson().fromJson(structureJson, AxonProjectModel::class.java)
  // val structure = Gson().fromJson(FileReader("complex-model.json"), AxonProjectModel::class.java)

  val emd =
      EventModellerDefinitionGenerator(structure)
          .build("uk.co.skipoles.clashcat.sagas.RegisterClanCommand", listOf())
  emd.entries.forEach(::println)
  //  val gen = VisualisationModelGenerator(emd).build()
  //  println(gen)

  val frame = JFrame("Test")
  frame.contentPane.layout = FlowLayout()
  frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

  val button = JButton("Start...")
  button.addActionListener { EventModelDialogWrapper(structure).show() }
  frame.add(button)
  frame.pack()
  frame.isVisible = true
}

private val structureJson =
    """
{
  "commands": [
    {
      "name": "uk.co.skipoles.clashcat.clashapi.ObtainPlayerDataFromCoCApiCommand",
      "shortName": "Obtain Player Data From Co C Api",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
          "shortName": "New Member Joined Saga"
        },
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
          "shortName": "Player Update Saga"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clashapi.ObtainClanDataFromCoCApiCommand",
      "shortName": "Obtain Clan Data From Co C Api",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
          "shortName": "Clan Registration Saga"
        },
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
          "shortName": "Clan Update Saga"
        }
      ],
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
    },
    {
      "name": "uk.co.skipoles.clashcat.clan.TrackClanCommand",
      "shortName": "Track Clan",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
          "shortName": "Clan Registration Saga"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.sagas.UpdatePlayerCommand",
      "shortName": "Update Player",
      "createdBy": [],
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
      "name": "uk.co.skipoles.clashcat.clashapi.DoesClanExistCommand",
      "shortName": "Does Clan Exist",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
          "shortName": "Clan Registration Saga"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.sagas.UpdateClanCommand",
      "shortName": "Update Clan",
      "createdBy": [],
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
      "name": "uk.co.skipoles.clashcat.sagas.RegisterClanCommand",
      "shortName": "Register Clan",
      "createdBy": [],
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
      "name": "uk.co.skipoles.clashcat.player.StopTrackingPlayerCommand",
      "shortName": "Stop Tracking Player",
      "createdBy": [
        {
          "type": "EventHandler",
          "name": "uk.co.skipoles.clashcat.player.PlayerTrackingService",
          "shortName": "Player Tracking Service"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.TrackPlayerCommand",
      "shortName": "Track Player",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
          "shortName": "New Member Joined Saga"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.UpdateFromPlayerDataCommand",
      "shortName": "Update From Player Data",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
          "shortName": "Player Update Saga"
        },
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
          "shortName": "New Member Joined Saga"
        }
      ],
      "handledBy": {
        "type": "Aggregate",
        "name": "uk.co.skipoles.clashcat.player.Player",
        "shortName": "Player",
        "events": [
          {
            "name": "uk.co.skipoles.clashcat.player.PlayerLastActiveEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.player.PlayerChangedWarPreferenceEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.player.PlayerChangedNameEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent"
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
      "name": "uk.co.skipoles.clashcat.clan.UpdateFromClanDataCommand",
      "shortName": "Update From Clan Data",
      "createdBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateSaga",
          "shortName": "Clan Update Saga"
        },
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationSaga",
          "shortName": "Clan Registration Saga"
        }
      ],
      "handledBy": {
        "type": "Aggregate",
        "name": "uk.co.skipoles.clashcat.clan.Clan",
        "shortName": "Clan",
        "events": [
          {
            "name": "uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.clan.MemberDetailsChangedEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.clan.MemberLeftEvent"
          },
          {
            "name": "uk.co.skipoles.clashcat.clan.ClanUpdatedEvent"
          }
        ],
        "commands": []
      }
    }
  ],
  "events": [
    {
      "name": "uk.co.skipoles.clashcat.player.PlayerLastActiveEvent",
      "shortName": "Player Last Active",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clan.NewMemberJoinedEvent",
      "shortName": "New Member Joined",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clan.Clan",
          "shortName": "Clan"
        }
      ],
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
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clan.ClanInformationChangedEvent",
      "shortName": "Clan Information Changed",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clan.Clan",
          "shortName": "Clan"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.PlayerChangedNameEvent",
      "shortName": "Player Changed Name",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clan.Clan",
          "shortName": "Clan"
        }
      ],
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
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.StoppedTrackingPlayerEvent",
      "shortName": "Stopped Tracking Player",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clan.Clan",
          "shortName": "Clan"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.PlayerMetricsChangedEvent",
      "shortName": "Player Metrics Changed",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.PlayerActivityChangedEvent",
      "shortName": "Player Activity Changed",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clan.ClanRegisteredEvent",
      "shortName": "Clan Registered",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clan.Clan",
          "shortName": "Clan"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.PlayerDonationsChangedEvent",
      "shortName": "Player Donations Changed",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.PlayerDonationsSeasonCompleteEvent",
      "shortName": "Player Donations Season Complete",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clashapi.PlayerDataObtainedEvent",
      "shortName": "Player Data Obtained",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clashapi.ClashOfClansService",
          "shortName": "Clash Of Clans Service"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.player.PlayerUpdatedEvent",
      "shortName": "Player Updated",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.player.Player",
          "shortName": "Player"
        }
      ],
      "handledBy": [
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateSaga",
          "shortName": "Player Update Saga",
          "events": [],
          "commands": []
        },
        {
          "type": "Saga",
          "name": "uk.co.skipoles.clashcat.sagas.NewMemberJoinedSaga",
          "shortName": "New Member Joined Saga",
          "events": [],
          "commands": []
        }
      ]
    },
    {
      "name": "uk.co.skipoles.clashcat.clashapi.ClanDataObtainedEvent",
      "shortName": "Clan Data Obtained",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clashapi.ClashOfClansService",
          "shortName": "Clash Of Clans Service"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clan.ClanUpdatedEvent",
      "shortName": "Clan Updated",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clan.Clan",
          "shortName": "Clan"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.sagas.PlayerUpdateRequestedEvent",
      "shortName": "Player Update Requested",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.sagas.SagasCommandHandler",
          "shortName": "Sagas Command Handler"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.sagas.ClanUpdateRequestedEvent",
      "shortName": "Clan Update Requested",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.sagas.SagasCommandHandler",
          "shortName": "Sagas Command Handler"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.sagas.ClanRegistrationReceivedEvent",
      "shortName": "Clan Registration Received",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.sagas.SagasCommandHandler",
          "shortName": "Sagas Command Handler"
        }
      ],
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
      "name": "uk.co.skipoles.clashcat.clashapi.ClanLookupCompletedEvent",
      "shortName": "Clan Lookup Completed",
      "createdBy": [
        {
          "type": "CommandHandler",
          "name": "uk.co.skipoles.clashcat.clashapi.ClashOfClansService",
          "shortName": "Clash Of Clans Service"
        }
      ],
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
    }
  ],
  "queries": [
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
      "name": "uk.co.skipoles.clashcat.player.TrackedPlayersQuery",
      "shortName": "Tracked Players",
      "handledBy": {
        "type": "QueryHandler",
        "name": "uk.co.skipoles.clashcat.player.PlayerSummaryProjection",
        "events": [],
        "commands": []
      }
    },
    {
      "name": "uk.co.skipoles.clashcat.player.PlayerSummaryQuery",
      "shortName": "Player Summary",
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
