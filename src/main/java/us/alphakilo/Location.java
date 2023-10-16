package us.alphakilo;

import lombok.Data;

@Data
public class Location {
    int bagId;
    String type;
    String id;
    String name;
    int slotId;
    int quantity;
}
