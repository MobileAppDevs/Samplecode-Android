//
//  ProjectModel.swift
//  Pods
//
//  Created by MacBook Air on 8/8/18.
//

import Foundation

public struct ProjectModel: Codable{
    public var id: String?
    public var address: String?
    public var budget: String?
    public var category_id: String?
    public var category_name: String?
    public var created_at: Int64?
    public var description: String?
    public var diemFee: String?
    public var equipment: String?
    public var hasPet: String?
    public var isActive: Bool?
    public var jobberDisplayFee: String?
    public var jobberFee: String?
    public var last_updated: Int?
    public var preferred_date: String?
    public var preferred_date_second: String?
    public var preferred_time: String?
    public var title: String?
    public var uid: String?
    public var user_id: String?
    public var user_name: String?
    public var latitude: String?
    public var longitude: String?
    public var image: [Image]?
    public var offers: [Offer]?
    public var offer_id: String?
    public var jobStatus: String?
    public var accept_date: Int64?
    public var complete_date: Int64?
    public var canceljob_date: Int64?
    public var dispute_date: Int64?
    public var DOD: String?
    public var user_profile: UserPublicModel?
    public var jobber_date: String?
    public var jobber_time: String?
    public var priceWithTax: String?
    public var ALC_description: String?
    public var ALC_header: String?
    public var order_num: Int64?

    
    



    





    

    


    
    
    
    public struct Image: Codable{
        public var contentType: String?
        public var fileName: String?
        public var imageurl: String?
    }
    
    public struct Offer: Codable{
        
        public var completion_date: Double?
        public var diem_fee: String?
        public var isDifferent: Bool?
        public var jobberUID: String?
        public var jobber_fee: String?
        public var listingID: String?
        public var message: String?
        public var offer_price: String?
        public var requestorUID: String?
        public var start_date: Double?
        public var start_time: Double?
        
        public var id: String?
        public var user_profile: UserPublicModel?
        
        
    }

}



    



