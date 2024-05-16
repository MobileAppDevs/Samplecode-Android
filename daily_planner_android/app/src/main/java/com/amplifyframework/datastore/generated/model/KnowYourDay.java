package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.core.model.ModelIdentifier;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the KnowYourDay type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "KnowYourDays", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class KnowYourDay implements Model {
  public static final QueryField ID = field("KnowYourDay", "id");
  public static final QueryField USER_ID = field("KnowYourDay", "userId");
  public static final QueryField HEALTH_COUNT = field("KnowYourDay", "healthCount");
  public static final QueryField PRODUCTIVITY_COUNT = field("KnowYourDay", "productivityCount");
  public static final QueryField MOOD_COUNT = field("KnowYourDay", "moodCount");
  public static final QueryField CREATED_AT = field("KnowYourDay", "createdAt");
  public static final QueryField UPDATED_AT = field("KnowYourDay", "updatedAt");
  public static final QueryField DATE = field("KnowYourDay", "date");
  public static final QueryField DEVICE_ID = field("KnowYourDay", "deviceId");
  public static final QueryField EMAIL = field("KnowYourDay", "email");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String userId;
  private final @ModelField(targetType="Int") Integer healthCount;
  private final @ModelField(targetType="Int") Integer productivityCount;
  private final @ModelField(targetType="Int") Integer moodCount;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime createdAt;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime updatedAt;
  private final @ModelField(targetType="AWSDate") Temporal.Date date;
  private final @ModelField(targetType="String") String deviceId;
  private final @ModelField(targetType="String") String email;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public Integer getHealthCount() {
      return healthCount;
  }
  
  public Integer getProductivityCount() {
      return productivityCount;
  }
  
  public Integer getMoodCount() {
      return moodCount;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public Temporal.Date getDate() {
      return date;
  }
  
  public String getDeviceId() {
      return deviceId;
  }
  
  public String getEmail() {
      return email;
  }
  
  private KnowYourDay(String id, String userId, Integer healthCount, Integer productivityCount, Integer moodCount, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, Temporal.Date date, String deviceId, String email) {
    this.id = id;
    this.userId = userId;
    this.healthCount = healthCount;
    this.productivityCount = productivityCount;
    this.moodCount = moodCount;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.date = date;
    this.deviceId = deviceId;
    this.email = email;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      KnowYourDay knowYourDay = (KnowYourDay) obj;
      return ObjectsCompat.equals(getId(), knowYourDay.getId()) &&
              ObjectsCompat.equals(getUserId(), knowYourDay.getUserId()) &&
              ObjectsCompat.equals(getHealthCount(), knowYourDay.getHealthCount()) &&
              ObjectsCompat.equals(getProductivityCount(), knowYourDay.getProductivityCount()) &&
              ObjectsCompat.equals(getMoodCount(), knowYourDay.getMoodCount()) &&
              ObjectsCompat.equals(getCreatedAt(), knowYourDay.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), knowYourDay.getUpdatedAt()) &&
              ObjectsCompat.equals(getDate(), knowYourDay.getDate()) &&
              ObjectsCompat.equals(getDeviceId(), knowYourDay.getDeviceId()) &&
              ObjectsCompat.equals(getEmail(), knowYourDay.getEmail());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getHealthCount())
      .append(getProductivityCount())
      .append(getMoodCount())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getDate())
      .append(getDeviceId())
      .append(getEmail())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("KnowYourDay {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("healthCount=" + String.valueOf(getHealthCount()) + ", ")
      .append("productivityCount=" + String.valueOf(getProductivityCount()) + ", ")
      .append("moodCount=" + String.valueOf(getMoodCount()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("deviceId=" + String.valueOf(getDeviceId()) + ", ")
      .append("email=" + String.valueOf(getEmail()))
      .append("}")
      .toString();
  }
  
  public static BuildStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static KnowYourDay justId(String id) {
    return new KnowYourDay(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      userId,
      healthCount,
      productivityCount,
      moodCount,
      createdAt,
      updatedAt,
      date,
      deviceId,
      email);
  }
  public interface BuildStep {
    KnowYourDay build();
    BuildStep id(String id);
    BuildStep userId(String userId);
    BuildStep healthCount(Integer healthCount);
    BuildStep productivityCount(Integer productivityCount);
    BuildStep moodCount(Integer moodCount);
    BuildStep createdAt(Temporal.DateTime createdAt);
    BuildStep updatedAt(Temporal.DateTime updatedAt);
    BuildStep date(Temporal.Date date);
    BuildStep deviceId(String deviceId);
    BuildStep email(String email);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String userId;
    private Integer healthCount;
    private Integer productivityCount;
    private Integer moodCount;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private Temporal.Date date;
    private String deviceId;
    private String email;
    public Builder() {
      
    }
    
    private Builder(String id, String userId, Integer healthCount, Integer productivityCount, Integer moodCount, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, Temporal.Date date, String deviceId, String email) {
      this.id = id;
      this.userId = userId;
      this.healthCount = healthCount;
      this.productivityCount = productivityCount;
      this.moodCount = moodCount;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
      this.date = date;
      this.deviceId = deviceId;
      this.email = email;
    }
    
    @Override
     public KnowYourDay build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new KnowYourDay(
          id,
          userId,
          healthCount,
          productivityCount,
          moodCount,
          createdAt,
          updatedAt,
          date,
          deviceId,
          email);
    }
    
    @Override
     public BuildStep userId(String userId) {
        this.userId = userId;
        return this;
    }
    
    @Override
     public BuildStep healthCount(Integer healthCount) {
        this.healthCount = healthCount;
        return this;
    }
    
    @Override
     public BuildStep productivityCount(Integer productivityCount) {
        this.productivityCount = productivityCount;
        return this;
    }
    
    @Override
     public BuildStep moodCount(Integer moodCount) {
        this.moodCount = moodCount;
        return this;
    }
    
    @Override
     public BuildStep createdAt(Temporal.DateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    @Override
     public BuildStep updatedAt(Temporal.DateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    @Override
     public BuildStep date(Temporal.Date date) {
        this.date = date;
        return this;
    }
    
    @Override
     public BuildStep deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    @Override
     public BuildStep email(String email) {
        this.email = email;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String userId, Integer healthCount, Integer productivityCount, Integer moodCount, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, Temporal.Date date, String deviceId, String email) {
      super(id, userId, healthCount, productivityCount, moodCount, createdAt, updatedAt, date, deviceId, email);
      
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder healthCount(Integer healthCount) {
      return (CopyOfBuilder) super.healthCount(healthCount);
    }
    
    @Override
     public CopyOfBuilder productivityCount(Integer productivityCount) {
      return (CopyOfBuilder) super.productivityCount(productivityCount);
    }
    
    @Override
     public CopyOfBuilder moodCount(Integer moodCount) {
      return (CopyOfBuilder) super.moodCount(moodCount);
    }
    
    @Override
     public CopyOfBuilder createdAt(Temporal.DateTime createdAt) {
      return (CopyOfBuilder) super.createdAt(createdAt);
    }
    
    @Override
     public CopyOfBuilder updatedAt(Temporal.DateTime updatedAt) {
      return (CopyOfBuilder) super.updatedAt(updatedAt);
    }
    
    @Override
     public CopyOfBuilder date(Temporal.Date date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder deviceId(String deviceId) {
      return (CopyOfBuilder) super.deviceId(deviceId);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
  }
  

  public static class KnowYourDayIdentifier extends ModelIdentifier<KnowYourDay> {
    private static final long serialVersionUID = 1L;
    public KnowYourDayIdentifier(String id) {
      super(id);
    }
  }
  
}
