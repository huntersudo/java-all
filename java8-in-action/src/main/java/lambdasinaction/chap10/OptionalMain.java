package lambdasinaction.chap10;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class OptionalMain {

    public String getCarInsuranceName(Optional<Person> person) {
        return person.flatMap(Person::getCar)
<<<<<<< HEAD
                     .flatMap(Car::getInsurance)
                     .map(Insurance::getName)
                     .orElse("Unknown");
    }

    public Set<String> getCarInsuranceNames(List<Person> persons) {
        return persons.stream()
                      .map(Person::getCar)
                      .map(optCar -> optCar.flatMap(Car::getInsurance))
                      .map(optInsurance -> optInsurance.map(Insurance::getName))
                      .flatMap(Optional::stream)
                      .collect(toSet());
=======
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }

//    public Set<String> getCarInsuranceNames(List<Person> persons) {
//        return persons.stream()
//                      .map(Person::getCar)
//                      .map(optCar -> optCar.flatMap(Car::getInsurance))
//                      .map(optInsurance -> optInsurance.map(Insurance::getName))
//                      .flatMap(Optional::stream)
//                      .collect(toSet());
//    }

    public static void main(String[] args) {


>>>>>>> develop
    }
}
