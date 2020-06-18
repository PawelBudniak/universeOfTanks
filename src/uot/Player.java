package uot;

import uot.objects.*;


import java.io.Serializable;


public class Player extends ForwardingTank implements Serializable {
    private static final long serialVersionUID = 3304568958729910057L;
    private final String name;

    public Player(String name, Tank tank) {
        super(tank);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
