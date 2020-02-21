package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.PylosBoard;
import be.kuleuven.pylos.game.PylosGameIF;
import be.kuleuven.pylos.game.PylosLocation;
import be.kuleuven.pylos.game.PylosSphere;
import be.kuleuven.pylos.player.PylosPlayer;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Ine on 5/05/2015.
 */
public class StudentPlayerRandomFit extends PylosPlayer{

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
		/* add a reserve sphere to a feasible random location */
        PylosSphere toMove = board.getReserve(this);
        PylosLocation[] locations = board.getLocations();

        PylosLocation toLocation = null;

        int random;
        while(toLocation == null){
            random = getRandom().nextInt(locations.length);

            if(locations[random].isUsable())
                toLocation = locations[random];
        }

        game.moveSphere(toMove, toLocation);
    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
		/* removeSphere a random sphere */
        PylosSphere[] spheres = board.getSpheres(this);
        PylosSphere toRemove = null;

        PylosLocation location;
        int random;
        while(toRemove == null){
            random = getRandom().nextInt(spheres.length);

            location = spheres[random].getLocation();
            if(location != null && spheres[random].canRemove())
                toRemove = spheres[random];
        }

        game.removeSphere(toRemove);
    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
		/* always pass */
        game.pass();
    }
}
