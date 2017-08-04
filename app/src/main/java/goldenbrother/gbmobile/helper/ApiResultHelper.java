package goldenbrother.gbmobile.helper;

import android.content.Context;

import goldenbrother.gbmobile.R;
import goldenbrother.gbmobile.model.AnnouncementModel;
import goldenbrother.gbmobile.model.Center;
import goldenbrother.gbmobile.model.ClubModel;
import goldenbrother.gbmobile.model.ClubPostMediaModel;
import goldenbrother.gbmobile.model.ClubPostMessageModel;
import goldenbrother.gbmobile.model.ClubPostModel;
import goldenbrother.gbmobile.model.Customer;
import goldenbrother.gbmobile.model.Discussion;
import goldenbrother.gbmobile.model.Dorm;
import goldenbrother.gbmobile.model.DormUser;
import goldenbrother.gbmobile.model.EventChatModel;
import goldenbrother.gbmobile.model.EventKindModel;
import goldenbrother.gbmobile.model.EventModel;
import goldenbrother.gbmobile.model.EventUserModel;
import goldenbrother.gbmobile.model.HospitalModel;
import goldenbrother.gbmobile.model.LaborModel;
import goldenbrother.gbmobile.model.ManagerModel;
import goldenbrother.gbmobile.model.Medical;
import goldenbrother.gbmobile.model.MedicalProcessStatusModel;
import goldenbrother.gbmobile.model.MedicalSymptomModel;
import goldenbrother.gbmobile.model.MedicalTrackProcessModel;
import goldenbrother.gbmobile.model.OnCallManagerModel;
import goldenbrother.gbmobile.model.PackageModel;
import goldenbrother.gbmobile.model.PersonalPickUpModel;
import goldenbrother.gbmobile.model.RepairKindModel;
import goldenbrother.gbmobile.model.RepairKindNumberModel;
import goldenbrother.gbmobile.model.RepairModel;
import goldenbrother.gbmobile.model.RepairRecordModel;
import goldenbrother.gbmobile.model.RoleInfo;
import goldenbrother.gbmobile.model.SatisfactionIssueModel;
import goldenbrother.gbmobile.model.SatisfactionQuestionModel;
import goldenbrother.gbmobile.model.ServiceChatModel;
import goldenbrother.gbmobile.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by asus on 2016/12/8.
 */

public class ApiResultHelper {
    // pre
    public static final int NO_NETWORK = -10;
    // in server
    public static final int SUCCESS = 1;
    public static final int FAIL = 0;
    public static final int EMPTY = 0;
    // response
    public static final int TIME_OUT = -3;
    public static final int PARSER_ERROR = -4;
    public static final int NO_RESPONSE = -5;

    // common
    public static int common(Context context, String response) {
        if (response == null) {
            ToastHelper.t(context, R.string.server_error);
            return NO_RESPONSE;
        }
        try {
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            switch (result) {
                case NO_NETWORK:
                    ToastHelper.t(context, R.string.no_network);
                    break;
                case TIME_OUT:
                    ToastHelper.t(context, R.string.time_out);
                    break;
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            ToastHelper.t(context, R.string.parse_error_json);
            return PARSER_ERROR;
        }
    }

    public static int commonCreate(String response) {
        try {
            return new JSONObject(response).getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int login(String response) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                JSONObject ji = j.getJSONObject("userInfo");
                int roleID = ji.getInt("roleID");
                RoleInfo.getInstance().setRoleID(roleID);
                if (roleID == -1) { // user
                    UserModel u = UserModel.getInstance();
                    u.setUserID(ji.getString("userID"));
                    u.setUserName(ji.getString("userName"));
                    u.setUserIDNumber(ji.getString("userIDNumber"));
                    u.setUserSex(ji.getString("userSex"));
                    u.setUserPhone(ji.getString("userPhone"));
                    u.setUserEmail(ji.getString("userEmail"));
                    u.setUserNationCode(ji.getString("userNationCode"));
                    u.setUserBirthday(ji.getString("userBirthday"));
                    String pic = "";
                    try {
                        pic = ji.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    u.setUserPicture(pic);
                } else if (roleID == 0) { // labor
                    LaborModel l = LaborModel.getInstance();
                    l.setUserID(ji.getString("userID"));
                    l.setUserName(ji.getString("userName"));
                    l.setUserIDNumber(ji.getString("userIDNumber"));
                    l.setServiceGroupID(ji.getInt("serviceGroupID"));
                    String pic = "";
                    try {
                        pic = ji.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    l.setUserPicture(pic);
                    l.setUserSex(ji.getString("userSex"));
                    l.setUserPhone(ji.getString("userPhone"));
                    l.setUserEmail(ji.getString("userEmail"));
                    l.setUserNationCode(ji.getString("userNationCode"));
                    l.setUserBirthday(ji.getString("userBirthday"));
                    l.setFlaborNo(ji.getString("flaborNo"));
                    l.setCustomerNo(ji.getString("customerNo"));
                    l.setWorkerNo(ji.getString("workerNo"));
                } else if (roleID == 1) { // manager
                    ManagerModel m = ManagerModel.getInstance();
                    m.setUserID(ji.getString("userID"));
                    m.setUserName(ji.getString("userName"));
                    m.setUserIDNumber(ji.getString("userIDNumber"));
                    String pic = "";
                    try {
                        pic = ji.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    m.setUserPicture(pic);
                    m.setUserSex(ji.getString("userSex"));
                    m.setUserPhone(ji.getString("userPhone"));
                    m.setUserEmail(ji.getString("userEmail"));
                    m.setUserNationCode(ji.getString("userNationCode"));
                    m.setUserBirthday(ji.getString("userBirthday"));
                    m.setTitle(ji.getString("title"));
                }
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getServiceChat(String response, ArrayList<ServiceChatModel> list_origin) {
        try {
            JSONObject j = new JSONObject(response);

            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<ServiceChatModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("chat");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    ServiceChatModel g = new ServiceChatModel();
                    g.setSGCNo(o.getInt("SGCNo"));
                    g.setServiceGroupID(o.getInt("serviceGroupID"));
                    g.setWriterID(o.getString("userID"));
                    g.setWriterName(o.getString("userName"));
                    String pic = "";
                    try {
                        pic = o.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    g.setWriterPicture(pic);
                    g.setContent(o.getString("content"));
                    g.setChatDate(o.getString("chatDate"));
                    list.add(g);
                }
                list_origin.clear();
                list_origin.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getEventList(String response, ArrayList<EventModel> list_origin) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<EventModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("event");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    EventModel em = new EventModel();
                    String pic = "";
                    try {
                        pic = o.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    em.setUserPicture(pic);
                    em.setServiceEventID(o.getInt("serviceEventID"));
                    em.setEventScore(o.getInt("eventScore"));
                    em.setChatCount(o.getInt("chatCount"));
                    em.setEventDescription(o.getString("eventDescription"));
                    em.setUserName(o.getString("userName"));
                    em.setWorkerNo(o.getString("workerNo"));
                    em.setUserID(o.getString("userID"));
                    em.setRoleID(0);
                    list.add(em);
                }
                list_origin.clear();
                list_origin.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getEventChat(String response, ArrayList<EventChatModel> list_origin, int ServiceEventID) {
        try {
            JSONObject j = new JSONObject(response);

            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<EventChatModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("chat");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    EventChatModel g = new EventChatModel();
                    g.setSECNo(o.getInt("eventChatID"));
                    g.setServiceEventID(o.getInt("serviceEventID"));
                    g.setWriterID(o.getString("userID"));
                    g.setWriterName(o.getString("userName"));
                    String pic = "";
                    try {
                        pic = o.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    g.setWriterPicture(pic);
                    g.setContent(o.getString("content"));
                    g.setChatDate(o.getString("chatDate"));
                    list.add(g);
                }
                list_origin.clear();
                list_origin.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadCloudGroupList(String response, List<ServiceChatModel> list_groupChat) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<ServiceChatModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("chat");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    ServiceChatModel gc = new ServiceChatModel();
                    gc.setServiceGroupID(o.getInt("serviceGroupID"));
                    gc.setChatCount(o.getInt("chatCount"));
                    gc.setWriterID(o.getString("userID"));
                    gc.setWriterName(o.getString("userName"));
                    gc.setWorkerNo(o.getString("workerNo"));
                    String pic = "";
                    try {
                        pic = o.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    gc.setWriterPicture(pic);
                    gc.setContent(o.getString("content"));
                    gc.setChatDate(o.getString("chatDate"));
                    list.add(gc);
                }
                list_groupChat.clear();
                list_groupChat.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadEventUserList(String response, ArrayList<EventUserModel> list_event_user) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<EventUserModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("user");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    EventUserModel eu = new EventUserModel();
                    eu.setUserID(o.getString("userID"));
                    String pic = "";
                    try {
                        pic = o.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    list.add(eu);
                }
                list_event_user.clear();
                list_event_user.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getMedicalTreatmentCode(String response, ArrayList<MedicalSymptomModel> list_kind, ArrayList<MedicalSymptomModel> list_detail) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                list_kind.clear();
                JSONArray arr_first = j.getJSONArray("first");
                for (int i = 0; i < arr_first.length(); i++) {
                    JSONObject o = arr_first.getJSONObject(i);
                    MedicalSymptomModel rm = new MedicalSymptomModel();
                    rm.setColumnName(o.getString("columnName"));
                    rm.setCode(o.getString("code"));
                    rm.setValue(o.getString("value"));
                    list_kind.add(rm);
                }
                list_detail.clear();
                JSONArray arr_second = j.getJSONArray("second");
                for (int i = 0; i < arr_second.length(); i++) {
                    JSONObject o = arr_second.getJSONObject(i);
                    MedicalSymptomModel rm = new MedicalSymptomModel();
                    rm.setColumnName(o.getString("columnName"));
                    rm.setCode(o.getString("code"));
                    rm.setValue(o.getString("value"));
                    list_detail.add(rm);
                }
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int uploadPicture(String response, HashMap<String, String> map) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                map.put("path", j.getString("url"));
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int checkIDNumber(String response, HashMap<String, String> map) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                JSONObject o = j.getJSONObject("userData");
                map.put("userName", o.getString("userName"));
                map.put("userSex", o.getString("userSex"));
                map.put("userBirthday", o.getString("userBirthday"));
                map.put("userEmail", o.getString("userEmail"));
                //map.put("userPhone", o.getString("userPhone"));
                map.put("userNationCode", o.getString("userNationCode"));
                map.put("areaNum", o.getString("areaNum"));

                int userType = j.getInt("userType");
                map.put("userType", userType + "");
                if (userType == 2) {
                    map.put("customerNo", o.getString("customerNo"));
                    map.put("flaborNo", o.getString("flaborNo"));
                }
                // userType = 3
                //map.put("title", o.getString("title"));
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadOnCallManager(String response, ArrayList<OnCallManagerModel> list_on_call_manager) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<OnCallManagerModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("chat");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    OnCallManagerModel ocm = new OnCallManagerModel();
                    ocm.setUserID(o.getString("userID"));
                    String pic = "";
                    try {
                        pic = o.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ocm.setUserPicture(pic);
                    ocm.setUserName(o.getString("userName"));
                    list.add(ocm);
                }
                list_on_call_manager.clear();
                list_on_call_manager.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadClubList(String response, ArrayList<ClubModel> list_club) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<ClubModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("club");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    ClubModel cm = new ClubModel();
                    cm.setClubID(o.getInt("clubID"));
                    cm.setClubName(o.getString("clubName"));
                    cm.setClubPicture(o.getString("clubPicture"));
                    list.add(cm);
                }
                list_club.clear();
                list_club.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadAllClubPostID(String response, ArrayList<Integer> list_club_id) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success"); // {"success":1,"id":[1,2,3,4,5]}
            if (success == 1) {
                ArrayList<Integer> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("id");
                for (int i = 0; i < arr.length(); i++) {
                    list.add(arr.getInt(i));
                }
                list_club_id.clear();
                list_club_id.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadClubPostList(String response, ArrayList<ClubPostModel> list_club_post) {
        try {
            ArrayList<ClubPostModel> list = new ArrayList<>();
            JSONObject obj = new JSONObject(response);
            int result = obj.getInt("success");
            if (result == ApiResultHelper.SUCCESS) {
                JSONArray arr = obj.getJSONArray("post");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject j = arr.getJSONObject(i);
                    ClubPostModel m = new ClubPostModel();
                    m.setClubPostID(j.getInt("clubPostID"));
                    String pic = "";
                    try {
                        pic = j.getString("postUserPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    m.setPostUserPicture(pic);
                    m.setPostUserName(j.getString("postUserName"));
                    m.setContent(j.getString("content"));
                    m.setCreateDate(j.getString("createDate"));
                    // media
                    JSONArray arr_media = j.getJSONArray("media");
                    for (int k = 0; k < arr_media.length(); k++) {
                        JSONObject j_media = arr_media.getJSONObject(k);
                        ClubPostMediaModel cpm = new ClubPostMediaModel();
                        cpm.setThumbNailPath(j_media.getString("thumbnailPath"));
                        cpm.setUrlPath(j_media.getString("urlPath"));
                        cpm.setType(j_media.getInt("type"));
                        cpm.setPosition(j_media.getInt("position"));
                        m.getMedias().add(cpm);
                    }
                    m.setMessageCount(j.getInt("messageCount"));
                    // message
                    JSONArray arr_message = j.getJSONArray("message");
                    for (int k = 0; k < arr_message.length(); k++) {
                        JSONObject j_message = arr_message.getJSONObject(k);
                        ClubPostMessageModel cpm = new ClubPostMessageModel();
                        cpm.setUserName(j_message.getString("userName"));
                        String pic2 = "";
                        try {
                            pic2 = j_message.getString("userPicture");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cpm.setUserPicture(pic2);
                        cpm.setMessage(j_message.getString("message"));
                        m.getMessages().add(cpm);
                    }
                    list.add(m);
                }
                list_club_post.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadClubPostMessage(String response, ArrayList<ClubPostMessageModel> list_message) {
        try {
            ArrayList<ClubPostMessageModel> list = new ArrayList<>();
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            if (result == ApiResultHelper.SUCCESS) {
                JSONArray arr = j.getJSONArray("message");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jj = arr.getJSONObject(i);
                    ClubPostMessageModel m = new ClubPostMessageModel();
                    m.setClubPostMessageID(jj.getInt("clubPostMessageID"));
                    m.setClubPostID(jj.getInt("clubPostID"));
                    m.setUserName(jj.getString("userName"));
                    String pic = "";
                    try {
                        pic = jj.getString("userPicture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    m.setUserPicture(pic);
                    m.setMessage(jj.getString("message"));
                    m.setCreateDate(jj.getString("createDate"));

                    list.add(m);
                }
                list_message.clear();
                list_message.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return ApiResultHelper.PARSER_ERROR;
        }
    }

    public static int loadAnnouncementList(String response, ArrayList<AnnouncementModel> list_announcement) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<AnnouncementModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("announcement");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    AnnouncementModel am = new AnnouncementModel();
                    am.setAnnouncementID(o.getInt("announcementID"));
                    am.setTitle(o.getString("title"));
                    am.setCreateDate(o.getString("createDate"));
                    am.setType(o.getInt("type"));
                    list.add(am);
                }
                list_announcement.clear();
                list_announcement.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadAnnouncement(String response, AnnouncementModel announcement) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                announcement.setAnnouncementID(j.getInt("announcementID"));
                announcement.setTitle(j.getString("title"));
                announcement.setContent(j.getString("content"));
                announcement.setCreateDate(j.getString("createDate"));
                announcement.setExpirationDate(j.getString("expirationDate"));
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadPackageList(String response, ArrayList<PackageModel> list_package) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<PackageModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("package");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    PackageModel pm = new PackageModel();
                    pm.setPackageID(o.getInt("packageID"));
                    pm.setDescription(o.getString("description"));
                    pm.setArriveDate(o.getString("arriveDate"));
                    list.add(pm);
                }
                list_package.clear();
                list_package.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int loadRepairKindNumber(String response, ArrayList<RepairKindNumberModel> list_repair_kind_number) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<RepairKindNumberModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("repairKind");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    RepairKindNumberModel rm = new RepairKindNumberModel();
                    rm.setRepairKindID(o.getInt("repairKindID"));
                    rm.setParentContent(o.getString("parentContent"));
                    rm.setNumber(o.getInt("number"));
                    list.add(rm);
                }
                list_repair_kind_number.clear();
                list_repair_kind_number.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getRepairRecordList(String response, ArrayList<RepairRecordModel> list_repair_record) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                ArrayList<RepairRecordModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("repairRecordList");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    RepairRecordModel rr = new RepairRecordModel();
                    rr.setRrsNo(o.getInt("rrsNo"));
                    rr.setHappenDate(o.getString("happenDate"));
                    rr.setEventDesc(o.getString("eventDesc"));
                    rr.setStatus(o.getString("status"));
                    list.add(rr);
                }
                list_repair_record.clear();
                list_repair_record.addAll(list);
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getRepairRecord(String response, RepairRecordModel r) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                r.setRrsNo(j.getInt("rrsno"));
                r.setCenterId(j.getString("centerId"));
                r.setDormID(j.getString("dormId"));
                r.setHappenDate(j.getString("happenDate"));
                r.setDutyID(j.getString("dutyId"));
                r.setDeadLineDate(j.getString("deadLineDate"));
                r.setPlace(j.getString("place"));
                r.setEventKindStr(j.getString("eventKindStr"));
                r.setEventDesc(j.getString("eventDesc"));
                r.setProcResult(j.getString("procResult"));
                r.setSourceDesc(j.getString("sourceDesc"));
                r.setSourceEventID(j.getInt("sourceEventID"));
                r.setCustomerNo(j.getString("customerNo"));
                r.setFlaborNo(j.getString("flaborno"));
                r.setStatus(j.getString("status"));
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getRepairArea(String response, ArrayList<RepairKindModel> list_area1, ArrayList<RepairKindModel> list_area2) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                JSONArray arr = j.getJSONArray("areas");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    RepairKindModel rk = new RepairKindModel();
                    rk.setId(o.getInt("id"));
                    rk.setParentId(0);
                    rk.setContent(o.getString("content"));
                    if (rk.getId() == 3 || rk.getId() == 4) {
                        list_area1.add(rk);
                    } else if (rk.getId() == 2 || rk.getId() == 1) {
                        list_area2.add(rk);
                    }
                }
                // swap id=4 to first
                if (list_area1.size() == 2 && list_area1.get(0).getId() != 4) {
                    Collections.swap(list_area1, 0, 1);
                }
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getRepairKind(String response, ArrayList<RepairKindModel> list_kind, ArrayList<RepairKindModel> list_detail) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                list_kind.clear();
                JSONArray arr_first = j.getJSONArray("first");
                for (int i = 0; i < arr_first.length(); i++) {
                    JSONObject o = arr_first.getJSONObject(i);
                    RepairKindModel rm = new RepairKindModel();
                    rm.setId(o.getInt("id"));
                    rm.setParentId(o.getInt("parentId"));
                    rm.setContent(o.getString("content"));
                    list_kind.add(rm);
                }
                list_detail.clear();
                JSONArray arr_second = j.getJSONArray("second");
                for (int i = 0; i < arr_second.length(); i++) {
                    JSONObject o = arr_second.getJSONObject(i);
                    RepairKindModel rm = new RepairKindModel();
                    rm.setId(o.getInt("id"));
                    rm.setParentId(o.getInt("parentId"));
                    rm.setContent(o.getString("content"));
                    list_detail.add(rm);
                }
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getSatisfactionIssueList(String response, ArrayList<SatisfactionIssueModel> list_si) {
        try {
            JSONObject j = new JSONObject(response);

            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<SatisfactionIssueModel> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("issues");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    SatisfactionIssueModel si = new SatisfactionIssueModel();
                    si.setSiNo(o.getInt("siNo"));
                    si.setName(o.getString("name"));
                    si.setEnable(o.getInt("enable"));
                    si.setCreateDate(o.getString("createDate"));

                    // add questions
                    ArrayList<SatisfactionQuestionModel> list_siq = new ArrayList<>();
                    JSONArray arr_questions = o.getJSONArray("questions");
                    for (int k = 0; k < arr_questions.length(); k++) {
                        JSONObject o_question = arr_questions.getJSONObject(k);
                        SatisfactionQuestionModel siq = new SatisfactionQuestionModel();
                        siq.setSiqNo(o_question.getInt("siqNo"));
                        siq.setSiNo(o_question.getInt("siNo"));
                        siq.setQuestion(o_question.getString("question"));
                        siq.setCreateDate(o_question.getString("createDate"));
                        list_siq.add(siq);
                    }
                    si.getQuestions().addAll(list_siq);
                    list.add(si);
                }
                list_si.clear();
                list_si.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int addSatisfactionIssueRecord(String response) {
        try {
            return new JSONObject(response).getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getDormUserInfo(String response, DormUser dormUser) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                dormUser.setUserName(j.getString("userName"));
                dormUser.setUserSex(j.getString("userSex"));
                dormUser.setUserBirthday(j.getString("userBirthday"));
                dormUser.setCustomerNo(j.getString("customerNo"));
                dormUser.setWorkerNo(j.getString("workerNo"));
                dormUser.setFlaborNo(j.getString("flaborNo"));
                dormUser.setDormID(j.getString("dormID"));
                dormUser.setRoomID(j.getString("roomID"));
                dormUser.setCenterDirectorID(j.getString("centerDirectorID"));
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getHospitalPickUp(String response, ArrayList<HospitalModel> list_hospital, ArrayList<PersonalPickUpModel> list_personal_pick_up) {
        try {
            JSONObject j = new JSONObject(response);
            int success = j.getInt("success");
            if (success == 1) {
                list_hospital.clear();
                list_hospital.add(new HospitalModel("0", "Select..."));
                JSONArray arr_hospital = j.getJSONArray("hospital");
                for (int i = 0; i < arr_hospital.length(); i++) {
                    JSONObject o = arr_hospital.getJSONObject(i);
                    list_hospital.add(new HospitalModel(o.getString("hospitalCode"), o.getString("hospitalName")));
                }
                list_personal_pick_up.clear();
                list_personal_pick_up.add(new PersonalPickUpModel("0", "Select..."));
                JSONArray arr_personal_pick_up = j.getJSONArray("personnelPickUp");
                for (int i = 0; i < arr_personal_pick_up.length(); i++) {
                    JSONObject o = arr_personal_pick_up.getJSONObject(i);
                    list_personal_pick_up.add(new PersonalPickUpModel(o.getString("userID"), o.getString("userName")));
                }
            }
            return success;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getMedicalFlaborList(String response, ArrayList<Medical> list_medical) {
        try {
            JSONObject j = new JSONObject(response);

            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<Medical> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("medicalFlabor");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Medical m = new Medical();
                    m.setMtrsno(o.getInt("mtrsno"));
                    m.getPatient().setFlaborNo(o.getString("flaborNo"));
                    m.getPatient().setName(o.getString("flaborName"));
                    m.getPatient().setCustomerNo(o.getString("customerNo"));
                    m.getPatient().setCustomerName(o.getString("customerName"));
                    m.getPatient().setRecordDate(o.getString("recordDate"));
                    list.add(m);
                }
                list_medical.clear();
                list_medical.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getMedicalRecord(String response, Medical medical) {
        try {
            JSONObject j = new JSONObject(response);

            int result = j.getInt("success");
            if (result == 1) {
                medical.getPatient().setAge(j.getInt("age"));
                medical.getPatient().setBloodType(j.getString("bloodType"));
                medical.getPatient().setRoomID(j.getString("roomID"));
                medical.getPatient().setFlaborNo(j.getString("flaborNo"));
                medical.getPatient().setDormID(j.getString("dormID"));
                medical.getPatient().setCenterDirectorID(j.getString("centerDirectorID"));
                medical.getPatient().setCustomerNo(j.getString("customerNo"));
                medical.getPatient().setRecordDate(j.getString("recordDate"));
                medical.setSignaturePath(j.getString("signature"));
                medical.setMedicalCertificatePath(j.getString("medicalCertificate"));
                medical.setServiceRecordPath(j.getString("serviceRecord"));
                medical.setDiagnosticCertificatePath(j.getString("diagnosticCertificate"));
                medical.setCreateId(j.getString("createId"));
                medical.setCreateTime(j.getString("createTime"));
                // symptom
                JSONArray arr_symptom = j.getJSONArray("medicalTreatmentRecordDetail");
                medical.getSymptom().clear();
                for (int i = 0; i < arr_symptom.length(); i++) {
                    JSONObject o = arr_symptom.getJSONObject(i);
                    MedicalSymptomModel ms = new MedicalSymptomModel();
                    ms.setCode(o.getString("symptomsType") + o.getString("symptomsTypeItem"));
                    medical.getSymptom().add(ms);
                }
                // process status
                JSONArray arr_process = j.getJSONArray("medicalProcessingRecord");
                medical.getProcessingStatus().clear();
                for (int i = 0; i < arr_process.length(); i++) {
                    JSONObject o = arr_process.getJSONObject(i);
                    MedicalProcessStatusModel mps = new MedicalProcessStatusModel();
                    mps.setProcessingStatus(o.getInt("processingStatus"));
                    mps.setProcessingStatusToHospitalID(o.getString("processingStatusToHospitalID"));
                    mps.setProcessingStatusHospitalSNo(o.getString("processingStatusHospitalSNo"));
                    mps.setProcessingStatusOtherMemo(o.getString("processingStatusOtherMemo"));
                    mps.setProcessingStatusMedicalCertificate(o.getString("processingStatusMedicalCertificate"));
                    medical.getProcessingStatus().add(mps);
                }
                // track process
                JSONArray arr_track = j.getJSONArray("medicalTreatmentProcessingRecord");
                medical.getTrackProcess().clear();
                for (int i = 0; i < arr_track.length(); i++) {
                    JSONObject o = arr_track.getJSONObject(i);
                    MedicalTrackProcessModel mtp = new MedicalTrackProcessModel();
                    mtp.setTreatmentStatus(o.getInt("treatmentStatus"));
                    mtp.setTreatmentMemo(o.getString("treatmentMemo"));
                    medical.getTrackProcess().add(mtp);
                }
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getCenterInfo(String response, ArrayList<Center> list_center) {
        try {
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<Center> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("center");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Center c = new Center();
                    c.setCenterID(o.getString("centerID"));
                    c.setCenterName(o.getString("centerName"));
                    list.add(c);
                }
                list_center.clear();
                list_center.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getDormInfo(String response, ArrayList<Dorm> list_dorm) {
        try {
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<Dorm> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("dorm");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Dorm d = new Dorm();
                    d.setDormID(o.getString("dormID"));
                    d.setDormName(o.getString("dormName"));
                    list.add(d);
                }
                list_dorm.clear();
                list_dorm.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getCustomerInfo(String response, ArrayList<Customer> list_customer) {
        try {
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<Customer> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("customer");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Customer c = new Customer();
                    c.setCustomerNo(o.getString("customerNo"));
                    c.setCustomerName(o.getString("customerName"));
                    list.add(c);
                }
                list_customer.clear();
                list_customer.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getDiscussionFlaborList(String response, ArrayList<Discussion> list_discussion) {
        try {
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            if (result == 1) {
                ArrayList<Discussion> list = new ArrayList<>();
                JSONArray arr = j.getJSONArray("discussionFlabor");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    Discussion d = new Discussion();
                    d.setDrsNo(o.getInt("drsno"));
                    d.setWorkerNo(o.getString("workerNo"));
                    d.setDiscussionPlace(o.getString("discussionPlace"));
                    d.setDormName(o.getString("dormName"));
                    d.setDiscussionDate(o.getString("discussionDate"));
                    d.setCustomerName(o.getString("customerName"));
                    d.setFlaborName(o.getString("flaborName"));
                    d.setCenterName(o.getString("centerName"));
                    list.add(d);
                }
                list_discussion.clear();
                list_discussion.addAll(list);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }

    public static int getDiscussionRecord(String response, Discussion discussion) {
        try {
            JSONObject j = new JSONObject(response);
            int result = j.getInt("success");
            if (result == 1) {
                discussion.setDepartment(j.getString("department"));
                discussion.setDiscussionDate(j.getString("discussionDate"));
                discussion.setDiscussionReason(j.getString("discussionReason"));
                discussion.setDiscussionPlace(j.getString("discussionPlace"));
                discussion.setDiscussionDesc(j.getString("discussionDesc"));
                discussion.setWorkerNo(j.getString("workerNo"));
                discussion.setFlaborName(j.getString("userName"));
                discussion.setServiceRecordPath(j.getString("serviceRecord"));
                discussion.setSignaturePath(j.getString("signature"));
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return PARSER_ERROR;
        }
    }
}
