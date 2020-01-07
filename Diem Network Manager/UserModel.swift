//
//  UserModel.swift
//  ApiManagerLibrary
//
//  Created by Nasim on 23/7/18.
//

import Foundation

public struct UserModel: Codable{
    public var uid: String?
    public var email: String?
    public var emailVerified: Bool?
    public var name: String?
    public var gender : String?
    public var firstName : String?
    public var lastName : String?
    public var isJobber : String?
    public var isRequester : String?
    public var phone : String?
    public var address : String?
    public var pictureURL : String?
    public var pictureURLThumbnail : String?
    public var rightForTheJob : String?
    public var whenImNotWorking : String?
    public var work_pictures : [WorkImage]?
    public var device_token : String?
    public var JoiningDate : String?
    public var sms_verified : String?
    public var wepay_verified : String?
    public var newAssignedJob : String?
    public var dodUser : String?

    


    


    
    
    
    public struct WorkImage: Codable{
        public var contentType: String?
        public var fileName: String?
        public var imageurl: String?
    }

}


public struct UserPublicModel: Codable{
    public var uid: String?
    public var email: String?
    public var emailVerified: Bool?
    public var name: String?
    public var gender : String?
    public var firstName : String?
    public var lastName : String?
    public var isJobber : String?
    public var isRequester : String?
    public var phone : String?
    public var address : String?
    public var pictureURL : String?
    public var pictureURLThumbnail : String?
    public var rightForTheJob : String?
    public var whenImNotWorking : String?
    public var work_pictures : [WorkImage]?
    public var device_token : String?
    public var JoiningDate : String?
    public var reviews : [Review]?
    public var sms_verified : String?
    public var newAssignedJob : String?
    public var dodUser : String?




    
    
    public struct WorkImage: Codable{
        public var contentType: String?
        public var fileName: String?
        public var imageurl: String?
    }
    public struct Review: Codable{
        public var badge: String?
        public var date: Double?
        public var feedback: String?
        public var name: String?
        public var rating: String?
        public var uid: String?

    }
    
    
    
}
