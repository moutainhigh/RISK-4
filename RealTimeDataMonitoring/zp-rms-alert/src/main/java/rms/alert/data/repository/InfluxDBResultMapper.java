//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
package rms.alert.data.repository;

import rms.alert.data.configs.InfluxDBConfig;
import org.influxdb.InfluxDBMapperException;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class InfluxDBResultMapper {

  @Autowired
  private InfluxDBConfig influxConfig;

  private static final ConcurrentMap<String, ConcurrentMap<String, Field>> CLASS_FIELD_CACHE = new ConcurrentHashMap();
  private static final int FRACTION_MIN_WIDTH = 0;
  private static final int FRACTION_MAX_WIDTH = 9;
  private static final boolean ADD_DECIMAL_POINT = true;
  private static final DateTimeFormatter ISO8601_FORMATTER;

  public InfluxDBResultMapper() {
  }

  public <T> List<T> toPOJO(QueryResult queryResult, Class<T> clazz) throws InfluxDBMapperException {
    Objects.requireNonNull(queryResult, "queryResult");
    Objects.requireNonNull(clazz, "clazz");
    this.throwExceptionIfMissingAnnotation(clazz);
    this.throwExceptionIfResultWithError(queryResult);
    this.cacheMeasurementClass(clazz);
    List<T> result = new LinkedList();
    String measurementName = influxConfig.getMeasurementName();

    queryResult.getResults().stream().filter((internalResult) -> {
      return Objects.nonNull(internalResult) && Objects.nonNull(internalResult.getSeries());
    }).forEach((internalResult) -> {
      internalResult.getSeries().stream().filter((series) -> {
        return series.getName().equals(measurementName);
      }).forEachOrdered((series) -> {
        this.parseSeriesAs(series, clazz, result);
      });
    });
    return result;
  }

  void throwExceptionIfMissingAnnotation(Class<?> clazz) {
    if (!clazz.isAnnotationPresent(Measurement.class)) {
      throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @" + Measurement.class.getSimpleName());
    }
  }

  void throwExceptionIfResultWithError(QueryResult queryResult) {
    if (queryResult.getError() != null) {
      throw new InfluxDBMapperException("InfluxDB returned an error: " + queryResult.getError());
    } else {
      queryResult.getResults().forEach((seriesResult) -> {
        if (seriesResult.getError() != null) {
          throw new InfluxDBMapperException("InfluxDB returned an error with Series: " + seriesResult.getError());
        }
      });
    }
  }

  void cacheMeasurementClass(Class<?>... classVarAgrs) {
    Class[] var2 = classVarAgrs;
    int var3 = classVarAgrs.length;

    for (int var4 = 0; var4 < var3; ++var4) {
      Class<?> clazz = var2[var4];
      if (!CLASS_FIELD_CACHE.containsKey(clazz.getName())) {
        ConcurrentMap<String, Field> initialMap = new ConcurrentHashMap();
        ConcurrentMap<String, Field> influxColumnAndFieldMap = (ConcurrentMap) CLASS_FIELD_CACHE.putIfAbsent(clazz.getName(), initialMap);
        if (influxColumnAndFieldMap == null) {
          influxColumnAndFieldMap = initialMap;
        }

        Field[] var8 = clazz.getDeclaredFields();
        int var9 = var8.length;

        for (int var10 = 0; var10 < var9; ++var10) {
          Field field = var8[var10];
          Column colAnnotation = (Column) field.getAnnotation(Column.class);
          if (colAnnotation != null) {
            ((ConcurrentMap) influxColumnAndFieldMap).put(colAnnotation.name(), field);
          }
        }
      }
    }

  }

  String getMeasurementName(Class<?> clazz) {
    return ((Measurement) clazz.getAnnotation(Measurement.class)).name();
  }

  <T> List<T> parseSeriesAs(Series series, Class<T> clazz, List<T> result) {
    int columnSize = series.getColumns().size();
    ConcurrentMap colNameAndFieldMap = (ConcurrentMap) CLASS_FIELD_CACHE.get(clazz.getName());

    try {
      T object = null;
      Iterator var7 = series.getValues().iterator();

      while (var7.hasNext()) {
        List<Object> row = (List) var7.next();

        for (int i = 0; i < columnSize; ++i) {
          Field correspondingField = (Field) colNameAndFieldMap.get(series.getColumns().get(i));
          if (correspondingField != null) {
            if (object == null) {
              object = clazz.newInstance();
            }

            this.setFieldValue(object, correspondingField, row.get(i));
          }
        }

        if (series.getTags() != null && !series.getTags().isEmpty()) {
          Iterator var14 = series.getTags().entrySet().iterator();

          while (var14.hasNext()) {
            Entry<String, String> entry = (Entry) var14.next();
            Field correspondingField = (Field) colNameAndFieldMap.get(entry.getKey());
            if (correspondingField != null) {
              this.setFieldValue(object, correspondingField, entry.getValue());
            }
          }
        }

        if (object != null) {
          result.add(object);
          object = null;
        }
      }

      return result;
    } catch (IllegalAccessException | InstantiationException var12) {
      throw new InfluxDBMapperException(var12);
    }
  }

  <T> void setFieldValue(T object, Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
    if (value != null) {
      Class<?> fieldType = field.getType();
      boolean oldAccessibleState = field.isAccessible();

      try {
        field.setAccessible(true);
        if (!this.fieldValueModified(fieldType, field, object, value) && !this.fieldValueForPrimitivesModified(fieldType, field, object, value) && !this.fieldValueForPrimitiveWrappersModified(fieldType, field, object, value)) {
          String msg = "Class '%s' field '%s' is from an unsupported type '%s'.";
          throw new InfluxDBMapperException(String.format(msg, object.getClass().getName(), field.getName(), field.getType()));
        }
      } catch (ClassCastException var11) {
        String msg = "Class '%s' field '%s' was defined with a different field type and caused a ClassCastException. The correct type is '%s' (current field value: '%s').";
        throw new InfluxDBMapperException(String.format(msg, object.getClass().getName(), field.getName(), value.getClass().getName(), value));
      } finally {
        field.setAccessible(oldAccessibleState);
      }

    }
  }

  <T> boolean fieldValueModified(Class<?> fieldType, Field field, T object, Object value) throws IllegalArgumentException, IllegalAccessException {
    if (String.class.isAssignableFrom(fieldType)) {
      field.set(object, String.valueOf(value));
      return true;
    } else if (Instant.class.isAssignableFrom(fieldType)) {
      Instant instant;
      if (value instanceof String) {
        instant = Instant.from(ISO8601_FORMATTER.parse(String.valueOf(value)));
      } else if (value instanceof Long) {
        instant = Instant.ofEpochMilli((Long) value);
      } else {
        if (!(value instanceof Double)) {
          throw new InfluxDBMapperException("Unsupported type " + field.getClass() + " for field " + field.getName());
        }

        instant = Instant.ofEpochMilli(((Double) value).longValue());
      }

      field.set(object, instant);
      return true;
    } else {
      return false;
    }
  }

  <T> boolean fieldValueForPrimitivesModified(Class<?> fieldType, Field field, T object, Object value) throws IllegalArgumentException, IllegalAccessException {
    if (Double.TYPE.isAssignableFrom(fieldType)) {
      field.setDouble(object, (Double) value);
      return true;
    } else if (Long.TYPE.isAssignableFrom(fieldType)) {
      field.setLong(object, ((Double) value).longValue());
      return true;
    } else if (Integer.TYPE.isAssignableFrom(fieldType)) {
      field.setInt(object, ((Double) value).intValue());
      return true;
    } else if (Boolean.TYPE.isAssignableFrom(fieldType)) {
      field.setBoolean(object, Boolean.valueOf(String.valueOf(value)));
      return true;
    } else {
      return false;
    }
  }

  <T> boolean fieldValueForPrimitiveWrappersModified(Class<?> fieldType, Field field, T object, Object value) throws IllegalArgumentException, IllegalAccessException {
    if (Double.class.isAssignableFrom(fieldType)) {
      field.set(object, value);
      return true;
    } else if (Long.class.isAssignableFrom(fieldType)) {
      field.set(object, ((Double) value).longValue());
      return true;
    } else if (Integer.class.isAssignableFrom(fieldType)) {
      field.set(object, ((Double) value).intValue());
      return true;
    } else if (Boolean.class.isAssignableFrom(fieldType)) {
      field.set(object, Boolean.valueOf(String.valueOf(value)));
      return true;
    } else {
      return false;
    }
  }

  static {
    ISO8601_FORMATTER = (new DateTimeFormatterBuilder()).appendPattern("yyyy-MM-dd'T'HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).appendPattern("X").toFormatter();
  }
}
