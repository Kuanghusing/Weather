package com.kuahusg.weather.model.Data;

import com.kuahusg.weather.model.Forecast;

/**
 * Created by kuahusg on 16-6-28.
 * com.kuahusg.weather.model
 */
public class WeatherResult {
    Query query;

    public Query getQuery() {
        return query;
    }


    public static class Query {
        Result results;

        public Result getResults() {
            return results;
        }
    }


    public static class Result {
        Channel channel;

        public Channel getChannel() {
            return channel;
        }
    }

    public static class Channel {
        String link;
        Wind wind;
        Astronomy astronomy;
        Item item;
        String lastBuildDate;

        public String getLink() {
            return link;
        }

        public Wind getWind() {
            return wind;
        }

        public Astronomy getAstronomy() {
            return astronomy;
        }

        public Item getItem() {

            return item;
        }

        public String getLastBuildDate() {
            return lastBuildDate;
        }
    }


    public static class Wind {
        String chill;
        String direction;
        String speed;

        public String getChill() {
            return chill;
        }

        public String getDirection() {
            return direction;
        }

        public String getSpeed() {
            return speed;
        }
    }

    public static class Astronomy {
        String sunrise;
        String sunset;

        public String getSunrise() {
            return sunrise;
        }

        public String getSunset() {
            return sunset;
        }
    }

    public static class Item {
        String link;
        String pubDate;
        Condition condition;
        Forecast[] forecast;

        public String getLink() {
            return link;
        }

        public String getPubDate() {
            return pubDate;
        }

        public Condition getCondition() {
            return condition;
        }

        public Forecast[] getForecast() {
            return forecast;
        }
    }

    public static class Condition {
        String date;
        String temp;
        String text;

        public String getDate() {
            return date;
        }

        public String getTemp() {
            return temp;
        }

        public String getText() {
            return text;
        }
    }
    /*public static class Forecast{
        String date;
        String day;
        String high;
        String low;
        String text;

        public String getDate() {
            return date;
        }

        public String getDay() {
            return day;
        }

        public String getHigh() {
            return high;
        }

        public String getLow() {
            return low;
        }

        public String getText() {
            return text;
        }
    }*/

}
