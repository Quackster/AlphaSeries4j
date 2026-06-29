package com.alphaseries.game.pet;

import com.alphaseries.protocol.PacketBuilder;
import com.alphaseries.protocol.WireEncoding;

public final class PetPayloads {
    private PetPayloads() {
    }

    public static String raceList(String productPet, String rowText, long rankIndex, long hcLevel) {
        long raceCount = 0L;
        PacketBuilder racePayload = PacketBuilder.create();
        for (String row : text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 4) {
                    long breedId = number(fields[1]);
                    long minRank = number(fields[2]);
                    long minHcRank = number(fields[3]);
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

    public static String inventoryList(String rowText) {
        long petCount = 0L;
        PacketBuilder petPayload = PacketBuilder.create();
        for (String row : text(rowText).split("\r", -1)) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                if (fields.length >= 4) {
                    String rowPayload = inventoryRow(fields);
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

    public static String inventoryRow(String[] fields) {
        long petId = number(field(fields, 0));
        if (petId <= 0L) {
            return "";
        }
        String petName = field(fields, 1);
        String petFigure = field(fields, 2).toLowerCase();
        long scratches = number(field(fields, 3));
        String[] figureParts = petFigure.split(" ", -1);
        long petType = figureParts.length >= 1 ? number(figureParts[0]) : 0L;
        long petRace = figureParts.length >= 2 ? number(figureParts[1]) : 0L;
        String petColor = figureParts.length >= 3 ? figureParts[2] : "";

        return PacketBuilder.message("0")
            .appendInt(petId)
            .appendString(petName)
            .appendInt(petType)
            .appendInt(petRace)
            .appendString("0" + petColor)
            .appendInt(scratches)
            .build();
    }

    public static long nameValidationCode(String candidateName) {
        String name = text(candidateName);
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

    public static String commandList(long petLevel, Object commandRows) {
        long resolvedLevel = Math.max(0L, petLevel);
        String[] rows = normalizeRows(commandRows);
        long allCount = 0L;
        long availableCount = 0L;
        PacketBuilder allPayload = PacketBuilder.create();
        PacketBuilder availablePayload = PacketBuilder.create();
        for (String row : rows) {
            if (!row.isEmpty()) {
                String[] fields = row.split("\t", -1);
                long commandId = number(field(fields, 0));
                long requiredLevel = number(field(fields, 1));
                if (commandId > 0L) {
                    allPayload.appendRaw('0').appendInt(commandId);
                    allCount++;
                    if (requiredLevel <= resolvedLevel) {
                        availablePayload.appendRaw('0').appendInt(commandId);
                        availableCount++;
                    }
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
            field(petFields, 1),
            field(petFields, 2),
            number(field(petFields, 3)),
            number(field(petFields, 4)),
            number(field(petFields, 5)),
            number(field(petFields, 6)),
            number(field(petFields, 7)),
            number(field(petFields, 8)),
            number(field(petFields, 9)),
            field(petFields, 10));
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
        if (text(commandAction).isEmpty()) {
            return "";
        }
        return PacketBuilder.message("IZ")
            .appendInt(botEntityId)
            .appendString(commandAction)
            .appendInt(commandId)
            .build();
    }

    private static String[] normalizeRows(Object rows) {
        if (rows == null) {
            return new String[0];
        }
        if (rows instanceof String[] rowArray) {
            return rowArray;
        }
        if (rows instanceof String[][] table) {
            String[] normalized = new String[table.length];
            for (int index = 0; index < table.length; index++) {
                normalized[index] = table[index] == null ? "" : String.join("\t", table[index]);
            }
            return normalized;
        }
        return text(rows).split("\r", -1);
    }

    private static String field(String[] fields, int index) {
        return fields != null && index >= 0 && index < fields.length ? text(fields[index]) : "";
    }

    private static long number(Object value) {
        return WireEncoding.parseLeadingLong(value);
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
