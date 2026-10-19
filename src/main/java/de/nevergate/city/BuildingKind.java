package de.nevergate.city;

public enum BuildingKind {
    BANK("bank", "Bank", "Bankmitarbeiter", 11, 9, 6),
    HOTEL("hotel", "Hotel", "Hotelrezeption", 13, 13, 11),
    BUYER("ankauf", "Ankauf", "Ankaufmitarbeiter", 11, 9, 6);

    public final String id, title, employee;
    public final int width, depth, height;
    BuildingKind(String id, String title, String employee, int width, int depth, int height) {
        this.id = id; this.title = title; this.employee = employee;
        this.width = width; this.depth = depth; this.height = height;
    }
    public static BuildingKind fromId(String id) {
        for (BuildingKind kind : values()) if (kind.id.equals(id)) return kind;
        return null;
    }
}
