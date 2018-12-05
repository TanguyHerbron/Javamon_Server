package fr.ensim.lemeeherbron;

import java.util.HashMap;

public class PokemonOnDisplay extends HashMap<String, HashMap<String, Pokemon>> {

    private static PokemonOnDisplay INSTANCE;

    private PokemonOnDisplay()
    {
        new HashMap<String, HashMap<String, Pokemon>>();
    }

    public static synchronized PokemonOnDisplay getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new PokemonOnDisplay();
        }

        return INSTANCE;
    }

}
