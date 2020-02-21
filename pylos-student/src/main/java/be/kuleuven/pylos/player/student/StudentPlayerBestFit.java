package be.kuleuven.pylos.player.student;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;

/**
 * Created by Ine on 25/02/2015.
 */
public class StudentPlayerBestFit extends PylosPlayer{
    boolean firstMove = true;

    @Override
    public void doMove(PylosGameIF game, PylosBoard board) {
        boolean gelukt = false;

        if(!gelukt) gelukt = tryToBlockSquareWith3FromOtherPlayer(game, board);

        if(!gelukt) gelukt = tryToMakeSquareFrom3(game, board);

        if(!gelukt) gelukt = tryToMoveSphereUp(game, board);

        if(!gelukt) gelukt = tryToBlockSquareWith2FromOtherPlayer(game, board);

        if(!gelukt) gelukt = tryToMakeSquareFrom2(game, board);


        if(!gelukt)
            doRandomMove(game, board);
    }

    @Override
    public void doRemove(PylosGameIF game, PylosBoard board) {
        if(!doRandomRemove(game, board, true))
            doRandomRemove(game, board, false);

    }

    @Override
    public void doRemoveOrPass(PylosGameIF game, PylosBoard board) {
        if(!doRandomRemove(game, board, true))
            game.pass();
    }

    //----------------------------------------------------------------------------------

    private boolean tryToMoveSphereUp(PylosGameIF game, PylosBoard board){
        PylosLocation toLocation;

        for(PylosLocation fromLocation : board.getLocations())
            if(!fromLocation.hasAbove() && fromLocation.getSphere() != null && fromLocation.getSphere().PLAYER_COLOR.toString().equals(this.PLAYER_COLOR.toString()))
                if(!makesOpenSquareForOpponent(board, fromLocation.getSphere())){
                    toLocation = getRandomLocationAbove(fromLocation, board);

                    if(toLocation != null && fromLocation.getSphere().canMoveTo(toLocation)) {
                        game.moveSphere(fromLocation.getSphere(), toLocation);
                        return true;
                    }
                }
        return false;
    }

    private PylosLocation getRandomLocationAbove(PylosLocation fromLocation, PylosBoard board){
        for(PylosLocation toLocation : board.getLocations())
            if(toLocation.Z > fromLocation.Z && toLocation.isUsable())
                return toLocation;
        return null;
    }

    private boolean makesOpenSquareForOpponent(PylosBoard board, PylosSphere toRemove){

        for(PylosSquare square : board.getAllSquares())
            if(square.getInSquare(this.OTHER) == 3)
                for(PylosLocation location : square.getLocations())
                    if(location == toRemove.getLocation())
                        return true;
        return false;
    }

    //----------------------------------------------------------------------------------

    private boolean doesRemoveMakeBadSquare(PylosBoard board, PylosLocation fromLocation){
        for(PylosSquare square : board.getAllSquares())
            for(PylosLocation location : square.getLocations())
                if(location == fromLocation)
                    if(square.getInSquare() == 3){
                        if(square.getInSquare(this.PLAYER_COLOR) == 3)
                            return false;
                        else
                            return true;
                    }
        return false;
    }

    private boolean doesRemoveMakeBadSquareV2(PylosBoard board, PylosLocation fromLocation){
        int aantalChecks;
        int checks = 0;

        if(isOnCorner(fromLocation))
            aantalChecks = 1;
        else if(isOnSide(fromLocation))
            aantalChecks = 2;
        else
            aantalChecks = 4;

        for(PylosSquare square : board.getAllSquares())
            if(contains(square.getLocations(), fromLocation))
                if(square.getInSquare(this.OTHER) == 3)
                    return true;
        return false;
    }

    private boolean doesRemoveMakeBadSquareV3(PylosBoard board, PylosLocation fromLocation){
        for(PylosSquare square : fromLocation.getSquares())
            if(square.getInSquare(this.OTHER) == 3)
                return true;
        return false;
    }

    //----------------------------------------------------------------------------------

    private boolean tryToBlockSquareWith3FromOtherPlayer(PylosGameIF game, PylosBoard board){
        PylosSquare[] squares = board.getAllSquares();

        for(PylosSquare square : squares) {
            if (square.getInSquare(this.OTHER) == 3 && square.getInSquare(this.PLAYER_COLOR) == 0)
                for (PylosLocation location : square.getLocations())
                    if (location.isUsable()) {
                        game.moveSphere(board.getReserve(this), location);
                        return true;
                    }
        }
        return false;
    }

    //----------------------------------------------------------------------------------

    private boolean tryToBlockSquareWith2FromOtherPlayer(PylosGameIF game, PylosBoard board){
        PylosSquare[] squares = board.getAllSquares();

        for(PylosSquare square : squares) {
            if (square.getInSquare(this.OTHER) == 2 && square.getInSquare(this.PLAYER_COLOR) == 0)
                for (PylosLocation location : square.getLocations())
                    if (location.isUsable()) {
                        game.moveSphere(board.getReserve(this), location);
                        return true;
                    }
        }
        return false;
    }

    //----------------------------------------------------------------------------------

    private boolean tryToMakeSquareFrom3(PylosGameIF game, PylosBoard board){
        PylosSquare[] squares = board.getAllSquares();

        for(PylosSquare square : squares) {
            if (square.getInSquare(this.PLAYER_COLOR) == 3 && square.getInSquare(this.OTHER) == 0) {
                for (PylosLocation location : square.getLocations())
                    if (location.isUsable()) {
                        game.moveSphere(board.getReserve(this), location);
                        return true;
                    }
            }
        }

        return false;
    }

    //----------------------------------------------------------------------------------

    private boolean tryToMakeSquareFrom2(PylosGameIF game, PylosBoard board){
        PylosSquare[] squares = board.getAllSquares();

        for(PylosSquare square : squares) {
            if (square.getInSquare(this.PLAYER_COLOR) == 2 && square.getInSquare(this.OTHER) == 0) {
                for (PylosLocation location : square.getLocations())
                    if (location.isUsable()) {
                        game.moveSphere(board.getReserve(this), location);
                        return true;
                    }
            }
        }

        return false;
    }

    //----------------------------------------------------------------------------------

    private void doRandomMove(PylosGameIF game, PylosBoard board) {
        /* add a reserve sphere to a feasible random location */
        PylosSphere toMove = board.getReserve(this);
        PylosLocation[] locations = board.getLocations();

        PylosLocation toLocation = null;

        if(firstMove) {
            toLocation = getFreeLocationInTheMiddle(board);
            firstMove = false;
        }

        int counter = 0;

        int random;
        while(toLocation == null){


            random = getRandom().nextInt(locations.length);

            if(locations[random].isUsable() && !doesRemoveMakeBadSquare(board, locations[random]))
                toLocation = locations[random];

            if(counter > 1000 && locations[random].isUsable())
                toLocation = locations[random];

            counter++;
        }

        game.moveSphere(toMove, toLocation);
    }

    private PylosLocation getFreeLocationInTheMiddle(PylosBoard board){
        for(PylosLocation location : board.getLocations()){
            if(location.X == 1 || location.X == 2)
                if(location.Y == 1 || location.Y == 2)
                    if(location.isUsable())
                        return location;
        }
        return null;
    }

    //----------------------------------------------------------------------------------

    private boolean doRandomRemove(PylosGameIF game, PylosBoard board, boolean check) {
        /* removeSphere a random sphere */
        PylosSphere[] spheres = board.getSpheres(this);
        PylosSphere toRemove = null;

        PylosLocation location;
        int random;
        int counter = 0;
        while(toRemove == null){
            random = getRandom().nextInt(spheres.length);

            location = spheres[random].getLocation();
            if(location != null && spheres[random].canRemove()) {
                if (check) {
                    if (!doesRemoveMakeBadSquare(board, spheres[random].getLocation()))
                        toRemove = spheres[random];
                }
                else
                    toRemove = spheres[random];
            }


            counter++;
            if(counter >= 1000)
                return false;
        }

        game.removeSphere(toRemove);
        return true;
    }

    //----------------------------------------------------------------------------------

    private boolean isOnCorner(PylosLocation location){
        if(location.Z == 0)
            if (location.X == 0 || location.X == 3)
                if (location.Y == 0 || location.Y == 3)
                    return true;

        if(location.Z == 1)
            if(location.X == 0 || location.X == 2)
                if (location.Y == 0 || location.Y == 2)
                    return true;

        if(location.Z == 2)
            return true;

        return false;
    }

    private boolean isOnSide(PylosLocation location){
        if(location.Z == 0) {
            if ((location.X == 0 || location.X == 3) && (location.Y == 1 || location.Y == 2))
                return true;
            if ((location.X == 1 || location.X == 2) && (location.Y == 0 || location.Y == 3))
                return true;
        }

        if(location.Z == 1) {
            if ((location.X == 0 || location.X == 2) && location.Y == 1)
                return true;
            if ((location.Y == 0 || location.Y == 2) && location.X == 1)
                return true;
        }

        return false;
    }

    private boolean contains(PylosLocation[] locations, PylosLocation location){
        for(PylosLocation l : locations)
            if(l == location)
                return true;
        return false;
    }
}
