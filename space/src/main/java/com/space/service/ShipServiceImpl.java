package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ShipServiceImpl implements ShipService {

    private final ShipRepository shipRepository;

    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> getShips(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
                               Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize,
                               Double minRating, Double maxRating) {

        Stream<Ship> streamOfList = shipRepository.findAll().stream();
        return streamOfList
                .filter(s -> name == null || s.getName().contains(name))
                .filter(s -> planet == null || s.getPlanet().contains(planet))
                .filter(s -> shipType == null || s.getShipType() == shipType)
                .filter(s -> after == null || s.getProdDate().getTime() >= after)
                .filter(s -> before == null || s.getProdDate().getTime() <= before)
                .filter(s -> isUsed == null || s.getUsed() == isUsed)
                .filter(s -> minSpeed == null || s.getSpeed() >= minSpeed)
                .filter(s -> maxSpeed == null || s.getSpeed() <= maxSpeed)
                .filter(s -> minCrewSize == null || s.getCrewSize() >= minCrewSize)
                .filter(s -> maxCrewSize == null || s.getCrewSize() <= maxCrewSize)
                .filter(s -> minRating == null || s.getRating() >= minRating)
                .filter(s -> maxRating == null || s.getRating() <= maxRating)
                .collect(Collectors.toList());
    }

    @Override
    public Ship createShip(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public Ship getShip(Long id) {
        Optional<Ship> optional = shipRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public Ship updateShip(Ship ship) {
        return shipRepository.save(ship);
    }

    @Override
    public void deleteShip(Long id) {
        shipRepository.deleteById(id);
    }

    public List<Ship> getShipsByPage(List<Ship> allShips, ShipOrder order, Integer pageNumber, Integer pageSize) {
        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        if (order == null) order = ShipOrder.ID;
        return allShips.stream().sorted(getComparator(order)).skip((long) pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());
    }

    private Comparator<Ship> getComparator(ShipOrder order) {
        switch (order.getFieldName()) {
            case "speed":
                return Comparator.comparing(Ship::getSpeed);
            case "prodDate":
                return Comparator.comparing(Ship::getProdDate);
            case "rating":
                return Comparator.comparing(Ship::getRating);
            default:
                return Comparator.comparing(Ship::getId);
        }
    }

}
