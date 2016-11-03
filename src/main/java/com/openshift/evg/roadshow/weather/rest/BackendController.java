package com.openshift.evg.roadshow.weather.rest;


import com.openshift.evg.roadshow.weather.model.Backend;
import com.openshift.evg.roadshow.weather.model.Coordinates;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides information about this backend
 *
 * Created by jmorales on 26/09/16.
 */
@RequestMapping("/ws/info")
@RestController
public class BackendController{

    @RequestMapping(method = RequestMethod.GET, value = "/", produces = "application/json")
    public Backend get() {
        Backend be = new Backend("weatherdata","Public weather data", new Coordinates("47.039304", "14.505178"), 5);
        be.setMaxZoom(6);
        be.setType(Backend.BACKEND_TYPE_TEMP);
        be.setScope(Backend.BACKEND_SCOPE_WITHIN);
        be.setVisible(true);
        return be;
    }
}
