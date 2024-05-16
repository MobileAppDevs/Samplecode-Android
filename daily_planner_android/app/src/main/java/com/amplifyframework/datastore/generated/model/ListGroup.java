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

/** This is an auto generated class representing the ListGroup type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ListGroups", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class ListGroup implements Model {
  public static final QueryField ID = field("ListGroup", "id");
  public static final QueryField NAME = field("ListGroup", "name");
  public static final QueryField CREATED_AT = field("ListGroup", "createdAt");
  public static final QueryField UPDATED_AT = field("ListGroup", "updatedAt");
  public static final QueryField USER_ID = field("ListGroup", "userId");
  public static final QueryField TITLE = field("ListGroup", "title");
  public static final QueryField DEVICE_ID = field("ListGroup", "deviceId");
  public static final QueryField EMAIL = field("ListGroup", "email");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime createdAt;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime updatedAt;
  private final @ModelField(targetType="String") String userId;
  private final @ModelField(targetType="String") String title;
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
  
  public String getName() {
      return name;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getDeviceId() {
      return deviceId;
  }
  
  public String getEmail() {
      return email;
  }
  
  private ListGroup(String id, String name, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, String userId, String title, String deviceId, String email) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.userId = userId;
    this.title = title;
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
      ListGroup listGroup = (ListGroup) obj;
      return ObjectsCompat.equals(getId(), listGroup.getId()) &&
              ObjectsCompat.equals(getName(), listGroup.getName()) &&
              ObjectsCompat.equals(getCreatedAt(), listGroup.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), listGroup.getUpdatedAt()) &&
              ObjectsCompat.equals(getUserId(), listGroup.getUserId()) &&
              ObjectsCompat.equals(getTitle(), listGroup.getTitle()) &&
              ObjectsCompat.equals(getDeviceId(), listGroup.getDeviceId()) &&
              ObjectsCompat.equals(getEmail(), listGroup.getEmail());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getUserId())
      .append(getTitle())
      .append(getDeviceId())
      .append(getEmail())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ListGroup {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
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
  public static ListGroup justId(String id) {
    return new ListGroup(
      id,
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
      name,
      createdAt,
      updatedAt,
      userId,
      title,
      deviceId,
      email);
  }
  public interface BuildStep {
    ListGroup build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep createdAt(Temporal.DateTime createdAt);
    BuildStep updatedAt(Temporal.DateTime updatedAt);
    BuildStep userId(String userId);
    BuildStep title(String title);
    BuildStep deviceId(String deviceId);
    BuildStep email(String email);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String name;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private String userId;
    private String title;
    private String deviceId;
    private String email;
    public Builder() {
      
    }
    
    private Builder(String id, String name, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, String userId, String title, String deviceId, String email) {
      this.id = id;
      this.name = name;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
      this.userId = userId;
      this.title = title;
      this.deviceId = deviceId;
      this.email = email;
    }
    
    @Override
     public ListGroup build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ListGroup(
          id,
          name,
          createdAt,
          updatedAt,
          userId,
          title,
          deviceId,
          email);
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
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
     public BuildStep userId(String userId) {
        this.userId = userId;
        return this;
    }
    
    @Override
     public BuildStep title(String title) {
        this.title = title;
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
    private CopyOfBuilder(String id, String name, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, String userId, String title, String deviceId, String email) {
      super(id, name, createdAt, updatedAt, userId, title, deviceId, email);
      
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
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
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
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
  

  public static class ListGroupIdentifier extends ModelIdentifier<ListGroup> {
    private static final long serialVersionUID = 1L;
    public ListGroupIdentifier(String id) {
      super(id);
    }
  }
  
}
