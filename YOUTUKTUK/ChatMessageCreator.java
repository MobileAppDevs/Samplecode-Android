package com.gofym.chat;

import com.getvee.ytt.common.constants.CommuterType;
import com.getvee.ytt.common.dto.RideFareDTO;
import com.gofym.utils.VeeObjectMapper;

/**
 * Created by Deep Verma on 24-05-2016.
 */
public class ChatMessageCreator {
    /**
     * This method is used to send Offer Ride Message to second party
     *
     * @param bookingId           id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendOfferRideMessage(Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction offerRideMessage = new YTTRideAction();
        offerRideMessage.setBookingId(bookingId);
        offerRideMessage.setPartnerUserName(partnerChatUserName);
        offerRideMessage.setStartTime(System.currentTimeMillis());
        offerRideMessage.setRideAction(YTTRideAction.YoutuktukConnectionType.OFFER_RIDE);
//        Parsing object and sending json in chat
        return VeeObjectMapper.convertToJson(offerRideMessage);
    }

    /**
     * This method is used to send Offer Ride Message to second party
     *
     * @param bookingId           id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendWithdrawRideMessage(Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction offerRideMessage = new YTTRideAction();
        offerRideMessage.setBookingId(bookingId);
        offerRideMessage.setPartnerUserName(partnerChatUserName);
        offerRideMessage.setStartTime(System.currentTimeMillis());
        offerRideMessage.setRideAction(YTTRideAction.YoutuktukConnectionType.WITHDRAW_RIDE);
        return VeeObjectMapper.convertToJson(offerRideMessage);
    }

    /**
     * This method is used to send Offer Ride Message to second party
     *
     * @param bookingId           id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @param commuterType        type driver or rider et enum  RIDER,DRIVER;
     * @return
     */
    public static String sendCancelRideMessage(Long bookingId, String partnerChatUserName, CommuterType commuterType) {
        // Creation of Object to set RideAction
        YTTRideAction offerRideMessage = new YTTRideAction();
        offerRideMessage.setBookingId(bookingId);
        offerRideMessage.setPartnerUserName(partnerChatUserName);
        offerRideMessage.setStartTime(System.currentTimeMillis());
        offerRideMessage.setRideAction(YTTRideAction.YoutuktukConnectionType.CANCEL_RIDE);
        offerRideMessage.setCommuterType(commuterType);
        return VeeObjectMapper.convertToJson(offerRideMessage);
    }

    /**
     * @param bookingId           id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendConfirmRideMessage(Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction sendConfirmRide = new YTTRideAction();
        sendConfirmRide.setBookingId(bookingId);
        sendConfirmRide.setPartnerUserName(partnerChatUserName);
        sendConfirmRide.setStartTime(System.currentTimeMillis());
        sendConfirmRide.setRideAction(YTTRideAction.YoutuktukConnectionType.CONFIRM_RIDE);
        return VeeObjectMapper.convertToJson(sendConfirmRide);
    }

    /**
     * @param bookingId           a unique id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendRejectRideMessage(Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction sendConfirmRide = new YTTRideAction();
        sendConfirmRide.setBookingId(bookingId);
        sendConfirmRide.setPartnerUserName(partnerChatUserName);
        sendConfirmRide.setStartTime(System.currentTimeMillis());
        sendConfirmRide.setRideAction(YTTRideAction.YoutuktukConnectionType.REJECT_RIDE);
        return VeeObjectMapper.convertToJson(sendConfirmRide);
    }

    /**
     * @param rideId              ride Id  a unique id created for ride
     * @param bookingId           unique id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendRideStartedMessage(Long rideId, Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction sendConfirmRide = new YTTRideAction();
        sendConfirmRide.setBookingId(bookingId);
        sendConfirmRide.setRideId(rideId);
        sendConfirmRide.setPartnerUserName(partnerChatUserName);
        sendConfirmRide.setStartTime(System.currentTimeMillis());
        sendConfirmRide.setRideAction(YTTRideAction.YoutuktukConnectionType.START_RIDE);
        return VeeObjectMapper.convertToJson(sendConfirmRide);
    }

    /**
     * @param rideId              ride Id  a unique id created for ride
     * @param bookingId           unique id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendRideStartedMessageInterCity(Long rideId, Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction sendConfirmRide = new YTTRideAction();
        sendConfirmRide.setBookingId(bookingId);
        sendConfirmRide.setRideId(rideId);
        sendConfirmRide.setPartnerUserName(partnerChatUserName);
        sendConfirmRide.setStartTime(System.currentTimeMillis());
        sendConfirmRide.setAction("INTERCITY");
        sendConfirmRide.setRideAction(YTTRideAction.YoutuktukConnectionType.START_RIDE);
        return VeeObjectMapper.convertToJson(sendConfirmRide);
    }

    /**
     * @param rideDTO             RideFareDTO object which is reuired to verify riders credentials
     * @param rideId              ride Id  a unique id created for ride
     * @param bookingId           unique id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendEndRidedMessage(RideFareDTO rideDTO, Long rideId, Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction sendConfirmRide = new YTTRideAction();
        sendConfirmRide.setRideFareDTO(rideDTO);

        sendConfirmRide.setBookingId(bookingId);
        sendConfirmRide.setRideId(rideId);
        sendConfirmRide.setPartnerUserName(partnerChatUserName);
        sendConfirmRide.setStartTime(System.currentTimeMillis());
        sendConfirmRide.setRideAction(YTTRideAction.YoutuktukConnectionType.END_RIDE);
        return VeeObjectMapper.convertToJson(sendConfirmRide);
    }

    /**
     * @param rideId              ride Id  a unique id created for ride
     * @param bookingId           unique id set by the user to book a ride
     * @param partnerChatUserName partner name whom user is Offering ride
     * @return
     */
    public static String sendEndRidedMessageInterCity(Long rideId, Long bookingId, String partnerChatUserName) {
        // Creation of Object to set RideAction
        YTTRideAction sendConfirmRide = new YTTRideAction();
        sendConfirmRide.setAction("INTERCITY");
        sendConfirmRide.setBookingId(bookingId);
        sendConfirmRide.setRideId(rideId);
        sendConfirmRide.setPartnerUserName(partnerChatUserName);
        sendConfirmRide.setStartTime(System.currentTimeMillis());
        sendConfirmRide.setRideAction(YTTRideAction.YoutuktukConnectionType.END_RIDE);
        return VeeObjectMapper.convertToJson(sendConfirmRide);
    }

    //  interface to create static final constants
    interface Messages {
        String SEND_OFFER_RIDE = "sendMessage";
    }
}
