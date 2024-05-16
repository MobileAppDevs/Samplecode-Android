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

/** This is an auto generated class representing the WaterInTake type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "WaterInTakes", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class WaterInTake implements Model {
  public static final QueryField ID = field("WaterInTake", "id");
  public static final QueryField DATE = field("WaterInTake", "date");
  public static final QueryField COUNT = field("WaterInTake", "count");
  public static final QueryField USER_ID = field("WaterInTake", "userId");
  public static final QueryField DEVICE_ID = field("WaterInTake", "deviceId");
  public static final QueryField EMAIL = field("WaterInTake", "email");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="AWSDate") Temporal.Date date;
  private final @ModelField(targetType="Int") Integer count;
  private final @ModelField(targetType="String") String userId;
  private final @ModelField(targetType="String") String deviceId;
  private final @ModelField(targetType="String") String email;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public Temporal.Date getDate() {
      return date;
  }
  
  public Integer getCount() {
      return count;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public String getDeviceId() {
      return deviceId;
  }
  
  public String getEmail() {
      return email;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private WaterInTake(String id, Temporal.Date date, Integer count, String userId, String deviceId, String email) {
    this.id = id;
    this.date = date;
    this.count = count;
    this.userId = userId;
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
      WaterInTake waterInTake = (WaterInTake) obj;
      return ObjectsCompat.equals(getId(), waterInTake.getId()) &&
              ObjectsCompat.equals(getDate(), waterInTake.getDate()) &&
              ObjectsCompat.equals(getCount(), waterInTake.getCount()) &&
              ObjectsCompat.equals(getUserId(), waterInTake.getUserId()) &&
              ObjectsCompat.equals(getDeviceId(), waterInTake.getDeviceId()) &&
              ObjectsCompat.equals(getEmail(), waterInTake.getEmail()) &&
              ObjectsCompat.equals(getCreatedAt(), waterInTake.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), waterInTake.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getDate())
      .append(getCount())
      .append(getUserId())
      .append(getDeviceId())
      .append(getEmail())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("WaterInTake {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("count=" + String.valueOf(getCount()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("deviceId=" + String.valueOf(getDeviceId()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
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
  public static WaterInTake justId(String id) {
    return new WaterInTake(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      date,
      count,
      userId,
      deviceId,
      email);
  }
  public interface BuildStep {
    WaterInTake build();
    BuildStep id(String id);
    BuildStep date(Temporal.Date date);
    BuildStep count(Integer count);
    BuildStep userId(String userId);
    BuildStep deviceId(String deviceId);
    BuildStep email(String email);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private Temporal.Date date;
    private Integer count;
    private String userId;
    private String deviceId;
    private String email;
    public Builder() {
      
    }
    
    private Builder(String id, Temporal.Date date, Integer count, String userId, String deviceId, String email) {
      this.id = id;
      this.date = date;
      this.count = count;
      this.userId = userId;
      this.deviceId = deviceId;
      this.email = email;
    }
    
    @Override
     public WaterInTake build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new WaterInTake(
          id,
          date,
          count,
          userId,
          deviceId,
          email);
    }
    
    @Override
     public BuildStep date(Temporal.Date date) {
        this.date = date;
        return this;
    }
    
    @Override
     public BuildStep count(Integer count) {
        this.count = count;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        this.userId = userId;
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
    private CopyOfBuilder(String id, Temporal.Date date, Integer count, String userId, String deviceId, String email) {
      super(id, date, count, userId, deviceId, email);
      
    }
    
    @Override
     public CopyOfBuilder date(Temporal.Date date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder count(Integer count) {
      return (CopyOfBuilder) super.count(count);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
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
  

  public static class WaterInTakeIdentifier extends ModelIdentifier<WaterInTake> {
    private static final long serialVersionUID = 1L;
    public WaterInTakeIdentifier(String id) {
      super(id);
    }
  }
  
}
