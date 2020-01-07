//
//  MyProjectsApiManager.swift
//  Pods
//
//  Created by MacBook Air on 8/8/18.
//

import Foundation


public class MyProjectsApiManager{
  
    
  
    
    private init(){}
    
    public static func getOpenProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/open", token: token, completion: completion)
    }
    
    public static func getAssignedProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/assigned", token: token, completion: completion)
    }
    
    public static func getCompletedProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/completed", token: token, completion: completion)
    }
    
    public static func getOtherProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/UnderDispute", token: token, completion: completion)
    }
    
    
    public static func getOpenJobberProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/jobber/open", token: token, completion: completion)
    }
    
    public static func getAssignedJobberProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/jobber/assigned", token: token, completion: completion)
    }
    
    public static func getCompletedJobberProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/jobber/completed", token: token, completion: completion)
    }
    
    public static func getOtherJobberProjects(token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        getProjectLisiting(jobType: "projects/jobber/UnderDispute", token: token, completion: completion)
    }
    
    /*
    public static func fetchProjectsForUrlString(_ urlString: String, token: String, completion: ((Results<[ProjectModel]>) -> Void)?){
        guard let url = URL(string: urlString) else {return}
        
        var request = URLRequest(url: url)
        
        print(url)
        request.httpMethod = "GET"
        request.addValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        print("Bearer \(token)")

        
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        let task = session.dataTask(with: request) { (responseData, response, responseError) in
            
            DispatchQueue.main.async {
                if let error = responseError {
                    completion?(.failure(error))
                } else
                    if let jsonData = responseData {
                        do {
                            var projects = try JSONDecoder().decode([ProjectModel].self, from: jsonData)
                            
                            projects = projects.sorted(by: { $0.created_at! > $1.created_at! })
                            completion?(.success(projects))
                            
                        } catch {
                            completion?(.failure(error))
                        }
                    }
                    else {
                        let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                        completion?(.failure(error))
                }
            }
        }
        task.resume()
    }
    */
    
    public static func  cancelJobPost(listingID: String, token: String, completion:((Results<String>) -> Void)?) {
        
        guard let url = URL(string: "\(baseUrl)/offer/cancel_job") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let postString = "listing_id=\(listingID)&reason=\("test")"
        print(postString)
        request.httpBody = postString.data(using: .utf8)
        
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    
                    print(error)
                    completion!(.failure(error))
                } else if let data = data {
                    let msg = String(decoding: data, as: UTF8.self)
                    completion!(.success(msg))
                    
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                    completion!(.failure(error))
                }
            }
            
            
        }
        task.resume()
        
    }
    
    public static func  cancelJobberJobPost(listingID: String, offerID: String, token: String, completion:((Results<String>) -> Void)?) {
        
        guard let url = URL(string: "\(baseUrl)/offer/jobber_cancel_job") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let postString = "listing_id=\(listingID)&offer_id=\(offerID)&reason=\("test")"
        print(postString)
        request.httpBody = postString.data(using: .utf8)
        
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")

        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    
                    print(error)
                    completion!(.failure(error))
                } else if let data = data {
                    let msg = String(decoding: data, as: UTF8.self)
                    completion!(.success(msg))
                    
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                    completion!(.failure(error))
                }
            }
            
            
        }
        task.resume()
        
    }
    
    public static func  disputeJobPost(listingID: String,offerID: String,reason: String, token: String, completion:((Results<String>) -> Void)?) {
        
        guard let url = URL(string: "\(baseUrl)/offer/disputes") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let postString = "listing_id=\(listingID)&offer_id=\(offerID)&reason=\(reason)"
        
        request.httpBody = postString.data(using: .utf8)
        
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")

        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    
                    print(error)
                    completion!(.failure(error))
                } else if let data = data {
                    let msg = String(decoding: data, as: UTF8.self)
                    completion!(.success(msg))
                    
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                    completion!(.failure(error))
                }
            }
            
            
        }
        task.resume()
        
    }
    
    /*
     - Get Project Listing
     */
    public static func getProjectLisiting(jobType: String, token: String, completion:((Results<[ProjectModel]>) -> Void)?) {
        
        guard let url = URL(string: "\(baseUrl)/user/project") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let postString = "job_type=\(jobType)"
        
        request.httpBody = postString.data(using: .utf8)
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    print(error)
                    completion!(.failure(error))
                } else if let data = data {
                    do {
                        let responseData = try JSONDecoder().decode([ProjectModel].self, from: data)
                        completion?(.success(responseData))
                    } catch {
                        completion?(.failure(error))
                    }
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                    completion!(.failure(error))
                }
            }
        }
        task.resume()
    }
    
    
    /*
     - Get Job Data on the basic of Job Id
     */
    public static func getJobDataOnId(listingID: String, userType: String, token: String, completion:((Results<[ProjectModel.Offer]>) -> Void)?) {
        
        guard let url = URL(string: "\(baseUrl)/user/project/details") else {return}
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        
        let postString = "listing_id=\(listingID)&userType=\(userType)"
        
        request.httpBody = postString.data(using: .utf8)
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            DispatchQueue.main.async {
                if let error = error {
                    print(error)
                    completion!(.failure(error))
                } else if let data = data {
                    do {
                        let responseData = try JSONDecoder().decode([ProjectModel.Offer].self, from: data)
                        completion?(.success(responseData))
                    } catch {
                        completion?(.failure(error))
                    }
                } else {
                    let error = NSError(domain: "", code: 0, userInfo: [NSLocalizedDescriptionKey : "Data was not retrieved from request"]) as Error
                    completion!(.failure(error))
                }
            }
        }
        task.resume()
    }
}
