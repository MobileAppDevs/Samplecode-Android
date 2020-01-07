//
//  JobsModel.swift
//  Pods
//
//  Created by MacBook Air on 4/8/18.
//

import UIKit

public struct JobsModel: Codable{
    public let id: String
    public var address: String?
    public var latitude: String?
    public var longitude: String?
    public let budget: String?
    public let category_id: String?
    public let category_name: String?
    public let created_at: Int?
    public let description: String?
    public let diemFee: String?
    public let equipment: String?
    public let hasPet: String?
    public let isActive: Bool?
    public let jobberDisplayFee: String?
    public let jobberFee: String?
    public let last_updated: Int?
    public let preferred_date: String?
    public let preferred_time: String?
    public let price: String?
    public let title: String?
    public let uid: String?
    public let user_id: String?
    public let user_name: String?
    
    public var image: [Image]?
    public var offers: [Offer]?
    
    public struct Image: Codable{
        public let contentType: String?
        public let filename: String?
        public let imageurl: String?
    }
    
    public struct Offer: Codable{
        public let completion_date: Double?
        public let diem_fee: String?
        public let isDifferent: Bool?
        public let jobberUID: String?
        public let jobber_fee: String?
        public let listingID: String?
        public let message: String?
        public let offer_price: String?
        public let requestorUID: String?
        public let start_date: Double?
        public let id: String?
    }

}


