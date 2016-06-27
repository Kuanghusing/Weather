package com.kuahusg.weather.model;

/**
 * Created by kuahusg on 16-6-27.
 * com.kuahusg.weather.model
 */
public class CitySearchResult {
    private Query query;

    public Query getQuery() {
        return query;
    }

    public static class Query{
        private String count;
        private String created;
        private String landg;
        private Results results;

        public Results getResults() {
            return results;
        }
    }


    public static class Results{
        private Place place;

        public Place getPlace() {
            return place;
        }
    }
    public static class Place{
        private String name;
        private String country;
        private String admin1;
        private String admin2;
        private String admin3;
        private String woeid;

        public String getName() {
            return name;
        }

        public String getCountry() {
            return country;
        }

        public String getAdmin1() {
            return admin1;
        }

        public String getAdmin2() {
            return admin2;
        }

        public String getAdmin3() {
            return admin3;
        }

        public String getWoeid() {
            return woeid;
        }
    }
}


