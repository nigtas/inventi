package lt.inventi.playground;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.bean.BeanLanguage;

import java.security.SecureRandom;

public class PlaygroundRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer:timer?period=1000").setHeader("Number", BeanLanguage.bean(PlaygroundRouteBuilder.class, "randomGenerator(${header.firedTime})"))
                    .choice()
                        .when(header("Number").isGreaterThan(800)).bean(PlaygroundRouteBuilder.class, "high(${header.Number})")
                        .when(header("Number").isLessThan(200)).bean(PlaygroundRouteBuilder.class, "low(${header.Number})")
                        .otherwise().bean(PlaygroundRouteBuilder.class, "other(${header.Number})");

    }

    public void print(String sr) {
        System.out.println(sr);
    }

    public void high(String nr) {
        System.out.println("Number is high!! " + nr);
    }

    public void low(String nr) {
        System.out.println("Number is low!! " + nr);
    }

    public void other(String nr) {
        System.out.println("Number is interesting!! " + nr);
    }

    public int randomGenerator(String str) {
        SecureRandom randomizer = new SecureRandom(str.getBytes());
        return randomizer.nextInt(1000);
    }
}
