package us.alphakilo;

import lombok.Data;

@Data
public class Character {
    private String id;
    private String accountName;
    private String name;
    private String server;
    private int level;
    private int gender;
    private int alliance;
    private int race;
    private int classId;
}
