package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;

import java.util.List;

public interface ShipService {
    List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                        Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating);
    Ship createShip(Ship ship);
    Ship getShip(Long id);
    Ship updateShip(Ship ship);
    void deleteShip(Long id);
    List<Ship> getShipsByPage(List<Ship> allShips, ShipOrder order, Integer pageNumber, Integer pageSize);

}