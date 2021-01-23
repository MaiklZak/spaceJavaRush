package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {

    private final ShipServiceImpl shipService;

    public ShipController(ShipServiceImpl shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Ship>> getShips(@RequestParam(name = "name", required = false) String name,
                                               @RequestParam(name = "planet", required = false) String planet,
                                               @RequestParam(name = "shipType", required = false) ShipType shipType,
                                               @RequestParam(name = "after", required = false) Long after,
                                               @RequestParam(name = "before", required = false) Long before,
                                               @RequestParam(name = "isUsed", required = false) Boolean isUsed,
                                               @RequestParam(name = "minSpeed", required = false) Double minSpeed,
                                               @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
                                               @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
                                               @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
                                               @RequestParam(name = "minRating", required = false) Double minRating,
                                               @RequestParam(name = "maxRating", required = false) Double maxRating,
                                               @RequestParam(name = "order", required = false) ShipOrder order,
                                               @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
                                               @RequestParam(name = "pageSize", required = false) Integer pageSize) {

        List<Ship> list = shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return new ResponseEntity<>(shipService.getShipsByPage(list, order, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/count")
    public Integer shipsCount(@RequestParam(name = "name", required = false) String name,
                              @RequestParam(name = "planet", required = false) String planet,
                              @RequestParam(name = "shipType", required = false) ShipType shipType,
                              @RequestParam(name = "after", required = false) Long after,
                              @RequestParam(name = "before", required = false) Long before,
                              @RequestParam(name = "isUsed", required = false) Boolean isUsed,
                              @RequestParam(name = "minSpeed", required = false) Double minSpeed,
                              @RequestParam(name = "maxSpeed", required = false) Double maxSpeed,
                              @RequestParam(name = "minCrewSize", required = false) Integer minCrewSize,
                              @RequestParam(name = "maxCrewSize", required = false) Integer maxCrewSize,
                              @RequestParam(name = "minRating", required = false) Double minRating,
                              @RequestParam(name = "maxRating", required = false) Double maxRating) {
        return shipService.getShips(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        if (id == null || id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = shipService.getShip(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        if (id == null || id < 1) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = shipService.getShip(id);
        if (ship == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        shipService.deleteShip(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        if (checkShip(ship)) {
            ship.setId(null);
            if (ship.getUsed() == null)
                ship.setUsed(false);
            ship.setRating(calculateRating(ship));
            shipService.createShip(ship);
            return new ResponseEntity<>(ship, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private double calculateRating(Ship ship) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        return ((double) Math.round(80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1) / (3019 - year + 1) * 100)) / 100;

    }

    @PostMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Ship> updateShip(@PathVariable("id") Long id, @RequestBody Ship ship) {
        if (id == null || id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Ship oldShip = shipService.getShip(id);
        if (oldShip == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        String name = ship.getName();
        if (name != null) oldShip.setName(name);

        String planet = ship.getPlanet();
        if (planet != null) oldShip.setPlanet(planet);

        ShipType shipType = ship.getShipType();
        if (shipType != null) oldShip.setShipType(shipType);

        Date prodDate = ship.getProdDate();
        if (prodDate != null) oldShip.setProdDate(prodDate);

        Boolean isUsed = ship.getUsed();
        if (isUsed != null) oldShip.setUsed(isUsed);

        Double speed = ship.getSpeed();
        if (speed != null) oldShip.setSpeed(speed);

        Integer crewSize = ship.getCrewSize();
        if (crewSize != null) oldShip.setCrewSize(crewSize);

        if (!checkShip(oldShip)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        oldShip.setRating(calculateRating(oldShip));
        shipService.updateShip(oldShip);
        return new ResponseEntity<>(oldShip, HttpStatus.OK);
    }

    public static boolean checkShip(Ship ship) {
        return checkRequired(ship) && validName(ship) && validPlanet(ship) && validProdDate(ship)
                                    && validSpeed(ship) && validCrewSize(ship);
    }

    private static boolean checkRequired(Ship ship) {
        return !(ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null
                || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null);
    }

    private static boolean validName(Ship ship) {
        String name = ship.getName();
        return !(name.isEmpty() || name.length() > 50);
    }

    private static boolean validPlanet(Ship ship) {
        String planet = ship.getPlanet();
        return !(planet.isEmpty() || planet.length() > 50);
    }

    private static boolean validProdDate(Ship ship) {
        Date prodDate = ship.getProdDate();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        return !(prodDate.getTime() < 0 || year < 2800 || year > 3019);
    }

    private static boolean validSpeed(Ship ship) {
        double speed = ((double)Math.round(ship.getSpeed()*100))/100;
        return !(speed < 0.01 || speed > 0.99);
    }

    private static boolean validCrewSize(Ship ship) {
        int crewSize = ship.getCrewSize();
        return !(crewSize < 1 || crewSize > 9999);
    }
}