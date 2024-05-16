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

/** This is an auto generated class representing the Tasks type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks", type = Model.Type.USER, version = 1, authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
public final class Tasks implements Model {
  public static final QueryField ID = field("Tasks", "id");
  public static final QueryField TITLE = field("Tasks", "title");
  public static final QueryField USER_ID = field("Tasks", "userId");
  public static final QueryField EMAIL_OR_PHONE = field("Tasks", "emailOrPhone");
  public static final QueryField DESCRIPTION = field("Tasks", "description");
  public static final QueryField DATE = field("Tasks", "date");
  public static final QueryField TIME = field("Tasks", "time");
  public static final QueryField REMINDER = field("Tasks", "reminder");
  public static final QueryField REPEAT_TYPE = field("Tasks", "repeatType");
  public static final QueryField IS_IMPORTANT = field("Tasks", "isImportant");
  public static final QueryField CREATED_AT = field("Tasks", "createdAt");
  public static final QueryField UPDATED_AT = field("Tasks", "updatedAt");
  public static final QueryField REPEAT_DAYS = field("Tasks", "repeatDays");
  public static final QueryField IS_COMPLETED = field("Tasks", "isCompleted");
  public static final QueryField TASK_TYPE = field("Tasks", "taskType");
  public static final QueryField LIST_GROUP_ID = field("Tasks", "listGroupId");
  public static final QueryField CUSTOM_TIME = field("Tasks", "customTime");
  public static final QueryField REPEAT_COUNT = field("Tasks", "repeatCount");
  public static final QueryField END_DATE = field("Tasks", "endDate");
  public static final QueryField NOTI_REQUEST_CODE = field("Tasks", "notiRequestCode");
  public static final QueryField NOTIFICATION_VISIBILITY_COUNT = field("Tasks", "notificationVisibilityCount");
  public static final QueryField DEVICE_ID = field("Tasks", "deviceId");
  public static final QueryField TASK_ID = field("Tasks", "taskId");
  public static final QueryField IS_TIME_SELECTED = field("Tasks", "isTimeSelected");
  public static final QueryField COUNTRY_CODE = field("Tasks", "countryCode");
  public static final QueryField REMINDER_REQUEST_CODE = field("Tasks", "reminderRequestCode");
  public static final QueryField EMAIL = field("Tasks", "email");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String") String title;
  private final @ModelField(targetType="String") String userId;
  private final @ModelField(targetType="String") String emailOrPhone;
  private final @ModelField(targetType="String") String description;
  private final @ModelField(targetType="AWSDate") Temporal.Date date;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime time;
  private final @ModelField(targetType="ReminderEnum") ReminderEnum reminder;
  private final @ModelField(targetType="RepeatType") RepeatType repeatType;
  private final @ModelField(targetType="Boolean") Boolean isImportant;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime createdAt;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime updatedAt;
  private final @ModelField(targetType="RepeatDays") List<RepeatDays> repeatDays;
  private final @ModelField(targetType="Boolean") Boolean isCompleted;
  private final @ModelField(targetType="TaskType") TaskType taskType;
  private final @ModelField(targetType="String") String listGroupId;
  private final @ModelField(targetType="AWSDateTime") Temporal.DateTime customTime;
  private final @ModelField(targetType="Int") Integer repeatCount;
  private final @ModelField(targetType="AWSDate") Temporal.Date endDate;
  private final @ModelField(targetType="Int") Integer notiRequestCode;
  private final @ModelField(targetType="Int") Integer notificationVisibilityCount;
  private final @ModelField(targetType="String") String deviceId;
  private final @ModelField(targetType="String") String taskId;
  private final @ModelField(targetType="Boolean") Boolean isTimeSelected;
  private final @ModelField(targetType="String") String countryCode;
  private final @ModelField(targetType="Int") Integer reminderRequestCode;
  private final @ModelField(targetType="String") String email;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getUserId() {
      return userId;
  }
  
  public String getEmailOrPhone() {
      return emailOrPhone;
  }
  
  public String getDescription() {
      return description;
  }
  
  public Temporal.Date getDate() {
      return date;
  }
  
  public Temporal.DateTime getTime() {
      return time;
  }
  
  public ReminderEnum getReminder() {
      return reminder;
  }
  
  public RepeatType getRepeatType() {
      return repeatType;
  }
  
  public Boolean getIsImportant() {
      return isImportant;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  public List<RepeatDays> getRepeatDays() {
      return repeatDays;
  }
  
  public Boolean getIsCompleted() {
      return isCompleted;
  }
  
  public TaskType getTaskType() {
      return taskType;
  }
  
  public String getListGroupId() {
      return listGroupId;
  }
  
  public Temporal.DateTime getCustomTime() {
      return customTime;
  }
  
  public Integer getRepeatCount() {
      return repeatCount;
  }
  
  public Temporal.Date getEndDate() {
      return endDate;
  }
  
  public Integer getNotiRequestCode() {
      return notiRequestCode;
  }
  
  public Integer getNotificationVisibilityCount() {
      return notificationVisibilityCount;
  }
  
  public String getDeviceId() {
      return deviceId;
  }
  
  public String getTaskId() {
      return taskId;
  }
  
  public Boolean getIsTimeSelected() {
      return isTimeSelected;
  }
  
  public String getCountryCode() {
      return countryCode;
  }
  
  public Integer getReminderRequestCode() {
      return reminderRequestCode;
  }
  
  public String getEmail() {
      return email;
  }
  
  private Tasks(String id, String title, String userId, String emailOrPhone, String description, Temporal.Date date, Temporal.DateTime time, ReminderEnum reminder, RepeatType repeatType, Boolean isImportant, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, List<RepeatDays> repeatDays, Boolean isCompleted, TaskType taskType, String listGroupId, Temporal.DateTime customTime, Integer repeatCount, Temporal.Date endDate, Integer notiRequestCode, Integer notificationVisibilityCount, String deviceId, String taskId, Boolean isTimeSelected, String countryCode, Integer reminderRequestCode, String email) {
    this.id = id;
    this.title = title;
    this.userId = userId;
    this.emailOrPhone = emailOrPhone;
    this.description = description;
    this.date = date;
    this.time = time;
    this.reminder = reminder;
    this.repeatType = repeatType;
    this.isImportant = isImportant;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.repeatDays = repeatDays;
    this.isCompleted = isCompleted;
    this.taskType = taskType;
    this.listGroupId = listGroupId;
    this.customTime = customTime;
    this.repeatCount = repeatCount;
    this.endDate = endDate;
    this.notiRequestCode = notiRequestCode;
    this.notificationVisibilityCount = notificationVisibilityCount;
    this.deviceId = deviceId;
    this.taskId = taskId;
    this.isTimeSelected = isTimeSelected;
    this.countryCode = countryCode;
    this.reminderRequestCode = reminderRequestCode;
    this.email = email;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Tasks tasks = (Tasks) obj;
      return ObjectsCompat.equals(getId(), tasks.getId()) &&
              ObjectsCompat.equals(getTitle(), tasks.getTitle()) &&
              ObjectsCompat.equals(getUserId(), tasks.getUserId()) &&
              ObjectsCompat.equals(getEmailOrPhone(), tasks.getEmailOrPhone()) &&
              ObjectsCompat.equals(getDescription(), tasks.getDescription()) &&
              ObjectsCompat.equals(getDate(), tasks.getDate()) &&
              ObjectsCompat.equals(getTime(), tasks.getTime()) &&
              ObjectsCompat.equals(getReminder(), tasks.getReminder()) &&
              ObjectsCompat.equals(getRepeatType(), tasks.getRepeatType()) &&
              ObjectsCompat.equals(getIsImportant(), tasks.getIsImportant()) &&
              ObjectsCompat.equals(getCreatedAt(), tasks.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), tasks.getUpdatedAt()) &&
              ObjectsCompat.equals(getRepeatDays(), tasks.getRepeatDays()) &&
              ObjectsCompat.equals(getIsCompleted(), tasks.getIsCompleted()) &&
              ObjectsCompat.equals(getTaskType(), tasks.getTaskType()) &&
              ObjectsCompat.equals(getListGroupId(), tasks.getListGroupId()) &&
              ObjectsCompat.equals(getCustomTime(), tasks.getCustomTime()) &&
              ObjectsCompat.equals(getRepeatCount(), tasks.getRepeatCount()) &&
              ObjectsCompat.equals(getEndDate(), tasks.getEndDate()) &&
              ObjectsCompat.equals(getNotiRequestCode(), tasks.getNotiRequestCode()) &&
              ObjectsCompat.equals(getNotificationVisibilityCount(), tasks.getNotificationVisibilityCount()) &&
              ObjectsCompat.equals(getDeviceId(), tasks.getDeviceId()) &&
              ObjectsCompat.equals(getTaskId(), tasks.getTaskId()) &&
              ObjectsCompat.equals(getIsTimeSelected(), tasks.getIsTimeSelected()) &&
              ObjectsCompat.equals(getCountryCode(), tasks.getCountryCode()) &&
              ObjectsCompat.equals(getReminderRequestCode(), tasks.getReminderRequestCode()) &&
              ObjectsCompat.equals(getEmail(), tasks.getEmail());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getUserId())
      .append(getEmailOrPhone())
      .append(getDescription())
      .append(getDate())
      .append(getTime())
      .append(getReminder())
      .append(getRepeatType())
      .append(getIsImportant())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .append(getRepeatDays())
      .append(getIsCompleted())
      .append(getTaskType())
      .append(getListGroupId())
      .append(getCustomTime())
      .append(getRepeatCount())
      .append(getEndDate())
      .append(getNotiRequestCode())
      .append(getNotificationVisibilityCount())
      .append(getDeviceId())
      .append(getTaskId())
      .append(getIsTimeSelected())
      .append(getCountryCode())
      .append(getReminderRequestCode())
      .append(getEmail())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Tasks {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("userId=" + String.valueOf(getUserId()) + ", ")
      .append("emailOrPhone=" + String.valueOf(getEmailOrPhone()) + ", ")
      .append("description=" + String.valueOf(getDescription()) + ", ")
      .append("date=" + String.valueOf(getDate()) + ", ")
      .append("time=" + String.valueOf(getTime()) + ", ")
      .append("reminder=" + String.valueOf(getReminder()) + ", ")
      .append("repeatType=" + String.valueOf(getRepeatType()) + ", ")
      .append("isImportant=" + String.valueOf(getIsImportant()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()) + ", ")
      .append("repeatDays=" + String.valueOf(getRepeatDays()) + ", ")
      .append("isCompleted=" + String.valueOf(getIsCompleted()) + ", ")
      .append("taskType=" + String.valueOf(getTaskType()) + ", ")
      .append("listGroupId=" + String.valueOf(getListGroupId()) + ", ")
      .append("customTime=" + String.valueOf(getCustomTime()) + ", ")
      .append("repeatCount=" + String.valueOf(getRepeatCount()) + ", ")
      .append("endDate=" + String.valueOf(getEndDate()) + ", ")
      .append("notiRequestCode=" + String.valueOf(getNotiRequestCode()) + ", ")
      .append("notificationVisibilityCount=" + String.valueOf(getNotificationVisibilityCount()) + ", ")
      .append("deviceId=" + String.valueOf(getDeviceId()) + ", ")
      .append("taskId=" + String.valueOf(getTaskId()) + ", ")
      .append("isTimeSelected=" + String.valueOf(getIsTimeSelected()) + ", ")
      .append("countryCode=" + String.valueOf(getCountryCode()) + ", ")
      .append("reminderRequestCode=" + String.valueOf(getReminderRequestCode()) + ", ")
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
  public static Tasks justId(String id) {
    return new Tasks(
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
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      userId,
      emailOrPhone,
      description,
      date,
      time,
      reminder,
      repeatType,
      isImportant,
      createdAt,
      updatedAt,
      repeatDays,
      isCompleted,
      taskType,
      listGroupId,
      customTime,
      repeatCount,
      endDate,
      notiRequestCode,
      notificationVisibilityCount,
      deviceId,
      taskId,
      isTimeSelected,
      countryCode,
      reminderRequestCode,
      email);
  }
  public interface BuildStep {
    Tasks build();
    BuildStep id(String id);
    BuildStep title(String title);
    BuildStep userId(String userId);
    BuildStep emailOrPhone(String emailOrPhone);
    BuildStep description(String description);
    BuildStep date(Temporal.Date date);
    BuildStep time(Temporal.DateTime time);
    BuildStep reminder(ReminderEnum reminder);
    BuildStep repeatType(RepeatType repeatType);
    BuildStep isImportant(Boolean isImportant);
    BuildStep createdAt(Temporal.DateTime createdAt);
    BuildStep updatedAt(Temporal.DateTime updatedAt);
    BuildStep repeatDays(List<RepeatDays> repeatDays);
    BuildStep isCompleted(Boolean isCompleted);
    BuildStep taskType(TaskType taskType);
    BuildStep listGroupId(String listGroupId);
    BuildStep customTime(Temporal.DateTime customTime);
    BuildStep repeatCount(Integer repeatCount);
    BuildStep endDate(Temporal.Date endDate);
    BuildStep notiRequestCode(Integer notiRequestCode);
    BuildStep notificationVisibilityCount(Integer notificationVisibilityCount);
    BuildStep deviceId(String deviceId);
    BuildStep taskId(String taskId);
    BuildStep isTimeSelected(Boolean isTimeSelected);
    BuildStep countryCode(String countryCode);
    BuildStep reminderRequestCode(Integer reminderRequestCode);
    BuildStep email(String email);
  }
  

  public static class Builder implements BuildStep {
    private String id;
    private String title;
    private String userId;
    private String emailOrPhone;
    private String description;
    private Temporal.Date date;
    private Temporal.DateTime time;
    private ReminderEnum reminder;
    private RepeatType repeatType;
    private Boolean isImportant;
    private Temporal.DateTime createdAt;
    private Temporal.DateTime updatedAt;
    private List<RepeatDays> repeatDays;
    private Boolean isCompleted;
    private TaskType taskType;
    private String listGroupId;
    private Temporal.DateTime customTime;
    private Integer repeatCount;
    private Temporal.Date endDate;
    private Integer notiRequestCode;
    private Integer notificationVisibilityCount;
    private String deviceId;
    private String taskId;
    private Boolean isTimeSelected;
    private String countryCode;
    private Integer reminderRequestCode;
    private String email;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String userId, String emailOrPhone, String description, Temporal.Date date, Temporal.DateTime time, ReminderEnum reminder, RepeatType repeatType, Boolean isImportant, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, List<RepeatDays> repeatDays, Boolean isCompleted, TaskType taskType, String listGroupId, Temporal.DateTime customTime, Integer repeatCount, Temporal.Date endDate, Integer notiRequestCode, Integer notificationVisibilityCount, String deviceId, String taskId, Boolean isTimeSelected, String countryCode, Integer reminderRequestCode, String email) {
      this.id = id;
      this.title = title;
      this.userId = userId;
      this.emailOrPhone = emailOrPhone;
      this.description = description;
      this.date = date;
      this.time = time;
      this.reminder = reminder;
      this.repeatType = repeatType;
      this.isImportant = isImportant;
      this.createdAt = createdAt;
      this.updatedAt = updatedAt;
      this.repeatDays = repeatDays;
      this.isCompleted = isCompleted;
      this.taskType = taskType;
      this.listGroupId = listGroupId;
      this.customTime = customTime;
      this.repeatCount = repeatCount;
      this.endDate = endDate;
      this.notiRequestCode = notiRequestCode;
      this.notificationVisibilityCount = notificationVisibilityCount;
      this.deviceId = deviceId;
      this.taskId = taskId;
      this.isTimeSelected = isTimeSelected;
      this.countryCode = countryCode;
      this.reminderRequestCode = reminderRequestCode;
      this.email = email;
    }
    
    @Override
     public Tasks build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Tasks(
          id,
          title,
          userId,
          emailOrPhone,
          description,
          date,
          time,
          reminder,
          repeatType,
          isImportant,
          createdAt,
          updatedAt,
          repeatDays,
          isCompleted,
          taskType,
          listGroupId,
          customTime,
          repeatCount,
          endDate,
          notiRequestCode,
          notificationVisibilityCount,
          deviceId,
          taskId,
          isTimeSelected,
          countryCode,
          reminderRequestCode,
          email);
    }
    
    @Override
     public BuildStep title(String title) {
        this.title = title;
        return this;
    }
    
    @Override
     public BuildStep userId(String userId) {
        this.userId = userId;
        return this;
    }
    
    @Override
     public BuildStep emailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
        return this;
    }
    
    @Override
     public BuildStep description(String description) {
        this.description = description;
        return this;
    }
    
    @Override
     public BuildStep date(Temporal.Date date) {
        this.date = date;
        return this;
    }
    
    @Override
     public BuildStep time(Temporal.DateTime time) {
        this.time = time;
        return this;
    }
    
    @Override
     public BuildStep reminder(ReminderEnum reminder) {
        this.reminder = reminder;
        return this;
    }
    
    @Override
     public BuildStep repeatType(RepeatType repeatType) {
        this.repeatType = repeatType;
        return this;
    }
    
    @Override
     public BuildStep isImportant(Boolean isImportant) {
        this.isImportant = isImportant;
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
     public BuildStep repeatDays(List<RepeatDays> repeatDays) {
        this.repeatDays = repeatDays;
        return this;
    }
    
    @Override
     public BuildStep isCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
        return this;
    }
    
    @Override
     public BuildStep taskType(TaskType taskType) {
        this.taskType = taskType;
        return this;
    }
    
    @Override
     public BuildStep listGroupId(String listGroupId) {
        this.listGroupId = listGroupId;
        return this;
    }
    
    @Override
     public BuildStep customTime(Temporal.DateTime customTime) {
        this.customTime = customTime;
        return this;
    }
    
    @Override
     public BuildStep repeatCount(Integer repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }
    
    @Override
     public BuildStep endDate(Temporal.Date endDate) {
        this.endDate = endDate;
        return this;
    }
    
    @Override
     public BuildStep notiRequestCode(Integer notiRequestCode) {
        this.notiRequestCode = notiRequestCode;
        return this;
    }
    
    @Override
     public BuildStep notificationVisibilityCount(Integer notificationVisibilityCount) {
        this.notificationVisibilityCount = notificationVisibilityCount;
        return this;
    }
    
    @Override
     public BuildStep deviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }
    
    @Override
     public BuildStep taskId(String taskId) {
        this.taskId = taskId;
        return this;
    }
    
    @Override
     public BuildStep isTimeSelected(Boolean isTimeSelected) {
        this.isTimeSelected = isTimeSelected;
        return this;
    }
    
    @Override
     public BuildStep countryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }
    
    @Override
     public BuildStep reminderRequestCode(Integer reminderRequestCode) {
        this.reminderRequestCode = reminderRequestCode;
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
    private CopyOfBuilder(String id, String title, String userId, String emailOrPhone, String description, Temporal.Date date, Temporal.DateTime time, ReminderEnum reminder, RepeatType repeatType, Boolean isImportant, Temporal.DateTime createdAt, Temporal.DateTime updatedAt, List<RepeatDays> repeatDays, Boolean isCompleted, TaskType taskType, String listGroupId, Temporal.DateTime customTime, Integer repeatCount, Temporal.Date endDate, Integer notiRequestCode, Integer notificationVisibilityCount, String deviceId, String taskId, Boolean isTimeSelected, String countryCode, Integer reminderRequestCode, String email) {
      super(id, title, userId, emailOrPhone, description, date, time, reminder, repeatType, isImportant, createdAt, updatedAt, repeatDays, isCompleted, taskType, listGroupId, customTime, repeatCount, endDate, notiRequestCode, notificationVisibilityCount, deviceId, taskId, isTimeSelected, countryCode, reminderRequestCode, email);
      
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder emailOrPhone(String emailOrPhone) {
      return (CopyOfBuilder) super.emailOrPhone(emailOrPhone);
    }
    
    @Override
     public CopyOfBuilder description(String description) {
      return (CopyOfBuilder) super.description(description);
    }
    
    @Override
     public CopyOfBuilder date(Temporal.Date date) {
      return (CopyOfBuilder) super.date(date);
    }
    
    @Override
     public CopyOfBuilder time(Temporal.DateTime time) {
      return (CopyOfBuilder) super.time(time);
    }
    
    @Override
     public CopyOfBuilder reminder(ReminderEnum reminder) {
      return (CopyOfBuilder) super.reminder(reminder);
    }
    
    @Override
     public CopyOfBuilder repeatType(RepeatType repeatType) {
      return (CopyOfBuilder) super.repeatType(repeatType);
    }
    
    @Override
     public CopyOfBuilder isImportant(Boolean isImportant) {
      return (CopyOfBuilder) super.isImportant(isImportant);
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
     public CopyOfBuilder repeatDays(List<RepeatDays> repeatDays) {
      return (CopyOfBuilder) super.repeatDays(repeatDays);
    }
    
    @Override
     public CopyOfBuilder isCompleted(Boolean isCompleted) {
      return (CopyOfBuilder) super.isCompleted(isCompleted);
    }
    
    @Override
     public CopyOfBuilder taskType(TaskType taskType) {
      return (CopyOfBuilder) super.taskType(taskType);
    }
    
    @Override
     public CopyOfBuilder listGroupId(String listGroupId) {
      return (CopyOfBuilder) super.listGroupId(listGroupId);
    }
    
    @Override
     public CopyOfBuilder customTime(Temporal.DateTime customTime) {
      return (CopyOfBuilder) super.customTime(customTime);
    }
    
    @Override
     public CopyOfBuilder repeatCount(Integer repeatCount) {
      return (CopyOfBuilder) super.repeatCount(repeatCount);
    }
    
    @Override
     public CopyOfBuilder endDate(Temporal.Date endDate) {
      return (CopyOfBuilder) super.endDate(endDate);
    }
    
    @Override
     public CopyOfBuilder notiRequestCode(Integer notiRequestCode) {
      return (CopyOfBuilder) super.notiRequestCode(notiRequestCode);
    }
    
    @Override
     public CopyOfBuilder notificationVisibilityCount(Integer notificationVisibilityCount) {
      return (CopyOfBuilder) super.notificationVisibilityCount(notificationVisibilityCount);
    }
    
    @Override
     public CopyOfBuilder deviceId(String deviceId) {
      return (CopyOfBuilder) super.deviceId(deviceId);
    }
    
    @Override
     public CopyOfBuilder taskId(String taskId) {
      return (CopyOfBuilder) super.taskId(taskId);
    }
    
    @Override
     public CopyOfBuilder isTimeSelected(Boolean isTimeSelected) {
      return (CopyOfBuilder) super.isTimeSelected(isTimeSelected);
    }
    
    @Override
     public CopyOfBuilder countryCode(String countryCode) {
      return (CopyOfBuilder) super.countryCode(countryCode);
    }
    
    @Override
     public CopyOfBuilder reminderRequestCode(Integer reminderRequestCode) {
      return (CopyOfBuilder) super.reminderRequestCode(reminderRequestCode);
    }
    
    @Override
     public CopyOfBuilder email(String email) {
      return (CopyOfBuilder) super.email(email);
    }
  }
  

  public static class TasksIdentifier extends ModelIdentifier<Tasks> {
    private static final long serialVersionUID = 1L;
    public TasksIdentifier(String id) {
      super(id);
    }
  }
  
}
