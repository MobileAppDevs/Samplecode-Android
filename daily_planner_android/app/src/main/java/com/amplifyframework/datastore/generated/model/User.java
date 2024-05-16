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

/** This is an auto generated class representing the User type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Users", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class User implements Model {
  public static final QueryField ID = field("User", "id");
  public static final QueryField NAME = field("User", "name");
  public static final QueryField EMAIL = field("User", "email");
  public static final QueryField PIC_URL = field("User", "picUrl");
  public static final QueryField IS_SOCIAL_LOGGED_IN = field("User", "isSocialLoggedIn");
  public static final QueryField SUBSCRIPTION_TYPE = field("User", "subscriptionType");
  public static final QueryField USER_ID = field("User", "userId");
  public static final QueryField IS_NOTIFICATION_ENABLED = field("User", "isNotificationEnabled");
  public static final QueryField IS_REMINDER_ENABLED = field("User", "isReminderEnabled");
  public static final QueryField IS_EMAIL_VERIFIED = field("User", "isEmailVerified");
  public static final QueryField DEVICE_ID = field("User", "deviceId");
  public static final QueryField COUNTRY_SELECTED = field("User", "countrySelected");
  public static final QueryField SUBSCRIPTION_VALID_UP_TO = field("User", "subscriptionValidUpTo");
  public static final QueryField SUBSCRIPTION_STATUS = field("User", "subscriptionStatus");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String name;
  private final @ModelField(targetType="String") String email;
  private final @ModelField(targetType="String") String picUrl;
  private final @ModelField(targetType="Boolean") Boolean isSocialLoggedIn;
  private final @ModelField(targetType="SubscriptionType") SubscriptionType subscriptionType;
  private final @ModelField(targetType="String") String userId;
  private final @ModelField(targetType="Boolean") Boolean isNotificationEnabled;
  private final @ModelField(targetType="Boolean") Boolean isReminderEnabled;
  private final @ModelField(targetType="Boolean") Boolean isEmailVerified;
  private final @ModelField(targetType="String") String deviceId;
  private final @ModelField(targetType="String") String countrySelected;
  private final @ModelField(targetType="AWSDate") Temporal.Date subscriptionValidUpTo;
  private final @ModelField(targetType="SubscriptionStatus") SubscriptionStatus subscriptionStatus;
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
  
  public String getName() {
      return name;
  }
  
  public String getEmail() {
      return email;
  }
  
  public String getPicUrl() {
      return picUrl;
  }
  
  public Boolean getIsSocialLoggedIn() {
      return isSocialLoggedIn;
  }
  
  public SubscriptionType getSubscriptionType() {
      return subscriptionType;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public Boolean getIsNotificationEnabled() {
      return isNotificationEnabled;
  }
  
  public Boolean getIsReminderEnabled() {
      return isReminderEnabled;
  }
  
  public Boolean getIsEmailVerified() {
      return isEmailVerified;
  }
  
  public String getDeviceId() {
      return deviceId;
  }
  
  public String getCountrySelected() {
      return countrySelected;
  }
  
  public Temporal.Date getSubscriptionValidUpTo() {
      return subscriptionValidUpTo;
  }
  
  public SubscriptionStatus getSubscriptionStatus() {
      return subscriptionStatus;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private User(String id, String name, String email, String picUrl, Boolean isSocialLoggedIn, SubscriptionType subscriptionType, String userId, Boolean isNotificationEnabled, Boolean isReminderEnabled, Boolean isEmailVerified, String deviceId, String countrySelected, Temporal.Date subscriptionValidUpTo, SubscriptionStatus subscriptionStatus) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.picUrl = picUrl;
    this.isSocialLoggedIn = isSocialLoggedIn;
    this.subscriptionType = subscriptionType;
    this.userId = userId;
    this.isNotificationEnabled = isNotificationEnabled;
    this.isReminderEnabled = isReminderEnabled;
    this.isEmailVerified = isEmailVerified;
    this.deviceId = deviceId;
    this.countrySelected = countrySelected;
    this.subscriptionValidUpTo = subscriptionValidUpTo;
    this.subscriptionStatus = subscriptionStatus;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      User user = (User) obj;
      return ObjectsCompat.equals(getId(), user.getId()) &&
              ObjectsCompat.equals(getName(), user.getName()) &&
              ObjectsCompat.equals(getEmail(), user.getEmail()) &&
              ObjectsCompat.equals(getPicUrl(), user.getPicUrl()) &&
              ObjectsCompat.equals(getIsSocialLoggedIn(), user.getIsSocialLoggedIn()) &&
              ObjectsCompat.equals(getSubscriptionType(), user.getSubscriptionType()) &&
              ObjectsCompat.equals(getUserId(), user.getUserId()) &&
              ObjectsCompat.equals(getIsNotificationEnabled(), user.getIsNotificationEnabled()) &&
              ObjectsCompat.equals(getIsReminderEnabled(), user.getIsReminderEnabled()) &&
              ObjectsCompat.equals(getIsEmailVerified(), user.getIsEmailVerified()) &&
              ObjectsCompat.equals(getDeviceId(), user.getDeviceId()) &&
              ObjectsCompat.equals(getCountrySelected(), user.getCountrySelected()) &&
              ObjectsCompat.equals(getSubscriptionValidUpTo(), user.getSubscriptionValidUpTo()) &&
              ObjectsCompat.equals(getSubscriptionStatus(), user.getSubscriptionStatus()) &&
              ObjectsCompat.equals(getCreatedAt(), user.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), user.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getEmail())
      .append(getPicUrl())
      .append(getIsSocialLoggedIn())
      .append(getSubscriptionType())
      .append(getUserId())
      .append(getIsNotificationEnabled())
      .append(getIsReminderEnabled())
      .append(getIsEmailVerified())
      .append(getDeviceId())
      .append(getCountrySelected())
      .append(getSubscriptionValidUpTo())
      .append(getSubscriptionStatus())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("User {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("email=" + String.valueOf(getEmail()) + ", ")
      .append("picUrl=" + String.valueOf(getPicUrl()) + ", ")
      .append("isSocialLoggedIn=" + String.valueOf(getIsSocialLoggedIn()) + ", ")
      .append("subscriptionType=" + String.valueOf(getSubscriptionType()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("isNotificationEnabled=" + String.valueOf(getIsNotificationEnabled()) + ", ")
      .append("isReminderEnabled=" + String.valueOf(getIsReminderEnabled()) + ", ")
      .append("isEmailVerified=" + String.valueOf(getIsEmailVerified()) + ", ")
      .append("deviceId=" + String.valueOf(getDeviceId()) + ", ")
      .append("countrySelected=" + String.valueOf(getCountrySelected()) + ", ")
      .append("subscriptionValidUpTo=" + String.valueOf(getSubscriptionValidUpTo()) + ", ")
      .append("subscriptionStatus=" + String.valueOf(getSubscriptionStatus()) + ", ")
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
  public static User justId(String id) {
    return new User(
      id,
      null,
      null,
      null,
      null,
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
      name,
      email,
      picUrl,
      isSocialLoggedIn,
      subscriptionType,
      userId,
      isNotificationEnabled,
      isReminderEnabled,
      isEmailVerified,
      deviceId,
      countrySelected,
      subscriptionValidUpTo,
      subscriptionStatus);
  }
  public interface BuildStep {
    User build();
    BuildStep id(String id);
    BuildStep name(String name);
    BuildStep email(String email);
    BuildStep picUrl(String picUrl);
    BuildStep isSocialLoggedIn(Boolean isSocialLoggedIn);
    BuildStep subscriptionType(SubscriptionType subscriptionType);
    BuildStep userId(String userId);
    BuildStep isNotificationEnabled(Boolean isNotificationEnabled);
    BuildStep isReminderEnabled(Boolean isReminderEnabled);
    BuildStep isEmailVerified(Boolean isEmailVerified);
    BuildStep deviceId(String deviceId);
    BuildStep countrySelected(String countrySelected);
    BuildStep subscriptionValidUpTo(Temporal.Date subscriptionValidUpTo);
    BuildStep subscriptionStatus(SubscriptionStatus subscriptionStatus);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String name;
    private String email;
    private String picUrl;
    private Boolean isSocialLoggedIn;
    private SubscriptionType subscriptionType;
    private String userId;
    private Boolean isNotificationEnabled;
    private Boolean isReminderEnabled;
    private Boolean isEmailVerified;
    private String deviceId;
    private String countrySelected;
    private Temporal.Date subscriptionValidUpTo;
    private SubscriptionStatus subscriptionStatus;
    public Builder() {
      
    }
    
    private Builder(String id, String name, String email, String picUrl, Boolean isSocialLoggedIn, SubscriptionType subscriptionType, String userId, Boolean isNotificationEnabled, Boolean isReminderEnabled, Boolean isEmailVerified, String deviceId, String countrySelected, Temporal.Date subscriptionValidUpTo, SubscriptionStatus subscriptionStatus) {
      this.id = id;
      this.name = name;
      this.email = email;
      this.picUrl = picUrl;
      this.isSocialLoggedIn = isSocialLoggedIn;
      this.subscriptionType = subscriptionType;
      this.userId = userId;
      this.isNotificationEnabled = isNotificationEnabled;
      this.isReminderEnabled = isReminderEnabled;
      this.isEmailVerified = isEmailVerified;
      this.deviceId = deviceId;
      this.countrySelected = countrySelected;
      this.subscriptionValidUpTo = subscriptionValidUpTo;
      this.subscriptionStatus = subscriptionStatus;
    }
    
    @Override
     public User build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new User(
          id,
          name,
          email,
          picUrl,
          isSocialLoggedIn,
          subscriptionType,
          userId,
          isNotificationEnabled,
          isReminderEnabled,
          isEmailVerified,
          deviceId,
          countrySelected,
          subscriptionValidUpTo,
          subscriptionStatus);
    }
    
    @Override
     public BuildStep name(String name) {
        this.name = name;
        return this;
    }
    
    @Override
     public BuildStep email(String email) {
        this.email = email;
        return this;
    }
    
    @Override
     public BuildStep picUrl(String picUrl) {
        this.picUrl = picUrl;
        return this;
    }
    
    @Override
     public BuildStep isSocialLoggedIn(Boolean isSocialLoggedIn) {
        this.isSocialLoggedIn = isSocialLoggedIn;
        return this;
    }
    
    @Override
     public BuildStep subscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        this.userId = userId;
        return this;
    }
    
    @Override
     public BuildStep isNotificationEnabled(Boolean isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
        return this;
    }
    
    @Override
     public BuildStep isReminderEnabled(Boolean isReminderEnabled) {
        this.isReminderEnabled = isReminderEnabled;
        return this;
    }
    
    @Override
     public BuildStep isEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
        return this;
    }
    
    @Override
     public BuildStep deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    @Override
     public BuildStep countrySelected(String countrySelected) {
        this.countrySelected = countrySelected;
        return this;
    }
    
    @Override
     public BuildStep subscriptionValidUpTo(Temporal.Date subscriptionValidUpTo) {
        this.subscriptionValidUpTo = subscriptionValidUpTo;
        return this;
    }
    
    @Override
     public BuildStep subscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
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
    private CopyOfBuilder(String id, String name, String email, String picUrl, Boolean isSocialLoggedIn, SubscriptionType subscriptionType, String userId, Boolean isNotificationEnabled, Boolean isReminderEnabled, Boolean isEmailVerified, String deviceId, String countrySelected, Temporal.Date subscriptionValidUpTo, SubscriptionStatus subscriptionStatus) {
      super(id, name, email, picUrl, isSocialLoggedIn, subscriptionType, userId, isNotificationEnabled, isReminderEnabled, isEmailVerified, deviceId, countrySelected, subscriptionValidUpTo, subscriptionStatus);
      
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
    
    @Override
     public CopyOfBuilder picUrl(String picUrl) {
      return (CopyOfBuilder) super.picUrl(picUrl);
    }
    
    @Override
     public CopyOfBuilder isSocialLoggedIn(Boolean isSocialLoggedIn) {
      return (CopyOfBuilder) super.isSocialLoggedIn(isSocialLoggedIn);
    }
    
    @Override
     public CopyOfBuilder subscriptionType(SubscriptionType subscriptionType) {
      return (CopyOfBuilder) super.subscriptionType(subscriptionType);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder isNotificationEnabled(Boolean isNotificationEnabled) {
      return (CopyOfBuilder) super.isNotificationEnabled(isNotificationEnabled);
    }
    
    @Override
     public CopyOfBuilder isReminderEnabled(Boolean isReminderEnabled) {
      return (CopyOfBuilder) super.isReminderEnabled(isReminderEnabled);
    }
    
    @Override
     public CopyOfBuilder isEmailVerified(Boolean isEmailVerified) {
      return (CopyOfBuilder) super.isEmailVerified(isEmailVerified);
    }
    
    @Override
     public CopyOfBuilder deviceId(String deviceId) {
      return (CopyOfBuilder) super.deviceId(deviceId);
    }
    
    @Override
     public CopyOfBuilder countrySelected(String countrySelected) {
      return (CopyOfBuilder) super.countrySelected(countrySelected);
    }
    
    @Override
     public CopyOfBuilder subscriptionValidUpTo(Temporal.Date subscriptionValidUpTo) {
      return (CopyOfBuilder) super.subscriptionValidUpTo(subscriptionValidUpTo);
    }
    
    @Override
     public CopyOfBuilder subscriptionStatus(SubscriptionStatus subscriptionStatus) {
      return (CopyOfBuilder) super.subscriptionStatus(subscriptionStatus);
    }
  }
  

  public static class UserIdentifier extends ModelIdentifier<User> {
    private static final long serialVersionUID = 1L;
    public UserIdentifier(String id) {
      super(id);
    }
  }
  
}
