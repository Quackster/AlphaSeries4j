package com.alphaseries.game.pet;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.util.NumberUtils;
import com.alphaseries.util.StringUtils;

import java.util.List;

public final class PetPayloads {
    private PetPayloads() {
    }

    public static String raceList(String productPet, String rowText, long rankIndex, long hcLevel) {
        long raceCount = 0L;
        PacketBuilder racePayload = PacketBuilder.create();
        for (String row : StringUtils.text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 4) {
                    long breedId = NumberUtils.parseLong(fields[1]);
                    long minRank = NumberUtils.parseLong(fields[2]);
                    long minHcRank = NumberUtils.parseLong(fields[3]);
                    if (rankIndex >= minRank && hcLevel >= minHcRank) {
                        racePayload.appendInt(breedId).appendRaw("II");
                        raceCount++;
                    }
                }
            }
        }
        return PacketBuilder.message("L{")
            .appendString(productPet)
            .appendInt(raceCount)
            .appendRaw(racePayload)
            .build();
    }

    public static String raceList(String productPet, List<PetRaceRow> rows, long rankIndex, long hcLevel) {
        long raceCount = 0L;
        PacketBuilder racePayload = PacketBuilder.create();
        if (rows != null) {
            for (PetRaceRow row : rows) {
                if (row != null && rankIndex >= row.minRank() && hcLevel >= row.minHcRank()) {
                    racePayload.appendInt(row.breed()).appendRaw("II");
                    raceCount++;
                }
            }
        }
        return PacketBuilder.message("L{")
            .appendString(productPet)
            .appendInt(raceCount)
            .appendRaw(racePayload)
            .build();
    }

    public static String inventoryList(List<PetInventoryRow> rows) {
        long petCount = 0L;
        PacketBuilder petPayload = PacketBuilder.create();
        if (rows != null) {
            for (PetInventoryRow row : rows) {
                if (row != null) {
                    String rowPayload = inventoryRow(row);
                    if (!rowPayload.isEmpty()) {
                        petPayload.appendRaw(rowPayload);
                        petCount++;
                    }
                }
            }
        }
        return PacketBuilder.message("IX")
            .appendInt(petCount)
            .appendRaw(petPayload)
            .build();
    }

    public static String inventoryRow(PetInventoryRow row) {
        if (row == null || row.petId() <= 0L) {
            return "";
        }
        return inventoryRow(row.petId(), row.name(), row.figure(), row.scratches());
    }

    public static String inventoryRow(String[] fields) {
        long petId = NumberUtils.parseLong(StringUtils.field(fields, 0));
        if (petId <= 0L) {
            return "";
        }
        return inventoryRow(
            petId,
            StringUtils.field(fields, 1),
            StringUtils.field(fields, 2),
            NumberUtils.parseLong(StringUtils.field(fields, 3)));
    }

    private static String inventoryRow(long petId, String petName, String figure, long scratches) {
        if (petId <= 0L) {
            return "";
        }
        String petFigure = StringUtils.text(figure).toLowerCase();
        String[] figureParts = petFigure.split(" ", -1);
        long petType = figureParts.length >= 1 ? NumberUtils.parseLong(figureParts[0]) : 0L;
        long petRace = figureParts.length >= 2 ? NumberUtils.parseLong(figureParts[1]) : 0L;
        String petColor = figureParts.length >= 3 ? figureParts[2] : "";

        return PacketBuilder.message("0")
            .appendInt(petId)
            .appendString(StringUtils.text(petName))
            .appendInt(petType)
            .appendInt(petRace)
            .appendString("0" + petColor)
            .appendInt(scratches)
            .build();
    }

    public static long nameValidationCode(String candidateName) {
        String name = StringUtils.text(candidateName);
        if (name.length() > 30) {
            return 1L;
        }
        if (name.length() < 1) {
            return 2L;
        }
        for (int index = 0; index < name.length(); index++) {
            char ch = name.charAt(index);
            boolean valid = (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
            if (!valid) {
                return 2L;
            }
        }
        return 0L;
    }

    public static String nameValidation(String candidateName) {
        return PacketBuilder.message("@d")
            .appendInt(nameValidationCode(candidateName))
            .build();
    }

    public static String packagePreview(long furnitureId, long petType, long petRace, String petColor) {
        return PacketBuilder.message("Ly")
            .appendInt(furnitureId)
            .appendInt(petType)
            .appendInt(petRace)
            .appendInt(NumberUtils.parseLong(petColor))
            .appendString(petColor)
            .build();
    }

    public static String packageNameValidation(long furnitureId, long validationCode, String petName) {
        return PacketBuilder.message("Lz")
            .appendInt(furnitureId)
            .appendInt(validationCode)
            .appendString(petName)
            .build();
    }

    public static String commandList(long petLevel, Object commandRows) {
        long resolvedLevel = Math.max(0L, petLevel);
        long allCount = 0L;
        long availableCount = 0L;
        PacketBuilder allPayload = PacketBuilder.create();
        PacketBuilder availablePayload = PacketBuilder.create();
        for (PetSettings.PetCommandRow row : PetSettings.commandRows(commandRows)) {
            if (row != null && row.commandId() > 0L) {
                allPayload.appendRaw('0').appendInt(row.commandId());
                allCount++;
                if (row.requiredLevel() <= resolvedLevel) {
                    availablePayload.appendRaw('0').appendInt(row.commandId());
                    availableCount++;
                }
            }
        }
        return PacketBuilder.message("I]")
            .appendInt(resolvedLevel)
            .appendInt(allCount)
            .appendRaw(allPayload)
            .appendInt(availableCount)
            .appendRaw(availablePayload)
            .build();
    }

    public static String status(long botEntityId, String[] petFields) {
        if (botEntityId <= 0L || petFields == null || petFields.length < 11) {
            return "";
        }
        return fullStatus(
            botEntityId,
            StringUtils.field(petFields, 1),
            StringUtils.field(petFields, 2),
            NumberUtils.parseLong(StringUtils.field(petFields, 3)),
            NumberUtils.parseLong(StringUtils.field(petFields, 4)),
            NumberUtils.parseLong(StringUtils.field(petFields, 5)),
            NumberUtils.parseLong(StringUtils.field(petFields, 6)),
            NumberUtils.parseLong(StringUtils.field(petFields, 7)),
            NumberUtils.parseLong(StringUtils.field(petFields, 8)),
            NumberUtils.parseLong(StringUtils.field(petFields, 9)),
            StringUtils.field(petFields, 10));
    }

    public static String status(long botEntityId, PetStatusRow petStatus) {
        if (botEntityId <= 0L || petStatus == null) {
            return "";
        }
        return fullStatus(
            botEntityId,
            petStatus.name(),
            petStatus.figure(),
            petStatus.level(),
            petStatus.experience(),
            petStatus.energy(),
            petStatus.nutrition(),
            petStatus.scratches(),
            petStatus.ageDays(),
            petStatus.ownerId(),
            petStatus.ownerName());
    }

    public static String fullStatus(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches,
        long petAgeDays,
        long ownerId,
        String ownerName
    ) {
        return experienceStatus(botEntityId, petName, petFigure, petLevel, petExperience, petEnergy, petNutrition, petScratches)
            + PacketBuilder.create()
                .appendInt(petAgeDays)
                .appendInt(ownerId)
                .appendString(ownerName)
                .build();
    }

    public static String experienceStatus(
        long botEntityId,
        String petName,
        String petFigure,
        long petLevel,
        long petExperience,
        long petEnergy,
        long petNutrition,
        long petScratches
    ) {
        return PacketBuilder.message("IY")
            .appendInt(botEntityId)
            .appendString(petName)
            .appendInt(petLevel)
            .appendInt(petExperience)
            .appendInt(petEnergy)
            .appendInt(petNutrition)
            .appendInt(petScratches)
            .appendString(petFigure)
            .build();
    }

    public static String experience(long botEntityId, long experienceDelta, long nextExperience) {
        return PacketBuilder.message("Ia")
            .appendInt(botEntityId)
            .appendInt(experienceDelta)
            .appendInt(nextExperience)
            .build();
    }

    public static String scratch(long botEntityId, long userId, long scratches, String petName, String petFigure) {
        return PacketBuilder.message("I^")
            .appendInt(botEntityId)
            .appendInt(userId)
            .appendInt(scratches)
            .appendString(petName)
            .appendString(petFigure)
            .build();
    }

    public static String commandAction(long botEntityId, String commandAction, long commandId) {
        if (StringUtils.text(commandAction).isEmpty()) {
            return "";
        }
        return PacketBuilder.message("IZ")
            .appendInt(botEntityId)
            .appendString(commandAction)
            .appendInt(commandId)
            .build();
    }

    public static String speech(long botEntityId, String speechText) {
        if (StringUtils.text(speechText).isEmpty()) {
            return "";
        }
        return PacketBuilder.message("@X")
            .appendInt(botEntityId)
            .appendString(speechText)
            .appendRaw('H')
            .build();
    }

}
