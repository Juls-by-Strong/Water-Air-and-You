import SwiftUI
import ComposeApp
import UserNotifications
import BackgroundTasks

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: "com.waterairandyou.poll",
            using: nil
        ) { task in
            self.handleBackgroundPoll(task as! BGAppRefreshTask)
        }
        return true
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.alert, .sound, .badge])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        completionHandler()
    }

    func scheduleNextBackgroundPoll() {
        let request = BGAppRefreshTaskRequest(identifier: "com.waterairandyou.poll")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 55)
        try? BGTaskScheduler.shared.submit(request)
    }

    func handleBackgroundPoll(_ task: BGAppRefreshTask) {
        scheduleNextBackgroundPoll()
        task.expirationHandler = {}
        BackgroundPoller.shared.pollOnce { success in
            task.setTaskCompleted(success: success.boolValue)
        }
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @Environment(\.scenePhase) var scenePhase

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onChange(of: scenePhase) { newPhase in
                    if newPhase == .background {
                        appDelegate.scheduleNextBackgroundPoll()
                    }
                }
        }
    }
}
