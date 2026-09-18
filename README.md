# 💼 FinExpert Pro — Commercial Profit & Business Management Suite

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/UI-Material%203-6750A4?style=for-the-badge&logo=materialdesign&logoColor=white" />
  <img src="https://img.shields.io/badge/Database-SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white" />
  <img src="https://img.shields.io/badge/Status-Production%20Ready-brightgreen?style=for-the-badge" />
</p>

---

## 📖 Overview

**FinExpert Pro** (স্মার্ট হিসাব ও বিজনেস স্যুট) is an enterprise-grade Android financial calculation and commerce management suite designed for retailers, wholesalers, merchants, and small-to-medium business owners. It provides instant profit-margin analytics, offline transaction logging, digital customer debt ledger (Khata) with direct WhatsApp reminders, and on-the-fly branded PDF invoice generation.

---

## 🌟 Key Features

### 1. 📊 Smart Profit & Margin Analytics Engine
* **Comprehensive Cost Matrix:** Calculate based on Unit Cost, Quantity (১ পিস থেকে হাজার পিস), Extra Overhead Expenses (Delivery, Packaging, Transport).
* **VAT & Discount Integration:** Real-time calculation of net revenue with tax and discount adjustments.
* **Target Margin Presets:** Instant preset chips (`+10%`, `+15%`, `+20%`, `+25%`, `+30%`, `+50%`) to auto-calculate recommended selling prices.
* **4-Grid Financial Matrix:** Displays Total Cost, Total Revenue, Gross Profit Margin %, and Markup Rate %.
* **Dynamic AI Commercial Insights:** Automatic business health feedback and margin recommendations based on profitability.

### 2. 🗄️ Offline SQLite Transaction Database
* **100% Offline & Secure:** All transaction data is stored locally using SQLite with zero external dependencies.
* **Live Search & Filter:** Instant search by product name or date.
* **Executive Summary Card:** Lifetime net profit tracking and total record counter.
* **Data Management:** Export any past transaction as a PDF invoice or delete records with confirmation dialogs.

### 3. 📄 Digital PDF Invoice & Receipt Generator
* **Native Android PDF Engine:** Built using `android.graphics.pdf.PdfDocument` (no heavy third-party SDKs required).
* **Shop Branding:** Automatically applies your custom Shop Name, contact information, and chosen currency symbol.
* **Instant Multi-Channel Sharing:** Share directly to WhatsApp, Gmail, Bluetooth thermal POS printers, or save to storage.

### 4. 📒 Digital Customer Due Ledger (বাকির খাতা)
* **Customer Debt Tracking:** Record customer names, phone numbers, due amounts, notes, and dates.
* **One-Tap WhatsApp Reminders:** Sends pre-formatted Bengali payment reminder messages directly to the customer's WhatsApp.
* **Direct Phone Call:** Quick-dial customer directly from the app.
* **Payment Status Toggle:** Toggle between "বাকি আছে (Due)" and "পরিশোধিত (Paid)".

### 5. ⚙️ Multi-Currency & Business Preferences
* **Multi-Currency Support:** Switch effortlessly between **৳ (BDT)**, **$ (USD)**, **₹ (INR)**, and **﷼ (SAR)**.
* **Shop Profile:** Set custom business name, address, and contact numbers for branded PDF receipts.

---

## 🏗️ Project Architecture & Directory Structure

```
app/src/main/
├── java/com/shofiqul/test_app/
│   ├── MainActivity.java            # Multi-tab controller & event handling
│   ├── DatabaseHelper.java          # SQLite OpenHelper for transactions & dues
│   ├── PdfInvoiceGenerator.java     # Canvas-based PDF receipt generator
│   ├── TransactionItem.java         # Financial transaction model
│   ├── DueItem.java                 # Customer due ledger model
│   ├── TransactionAdapter.java      # History list RecyclerView adapter
│   └── DueAdapter.java              # Customer due RecyclerView adapter
├── res/
│   ├── layout/
│   │   ├── activity_main.xml        # Modern 4-tab Material 3 dashboard layout
│   │   ├── item_transaction.xml    # History card view item
│   │   ├── item_due.xml            # Khata ledger card view item
│   │   └── dialog_add_due.xml      # Popup modal for adding customer dues
│   ├── drawable/                    # Vector icons, status pills, and gradients
│   ├── values/                      # Colors, themes, and string definitions
│   └── xml/file_paths.xml           # FileProvider paths for PDF sharing
└── AndroidManifest.xml              # Manifest with FileProvider configuration
```

---

## 🚀 Getting Started & Build Instructions

### Prerequisites
* **Android Studio:** Koala / Ladybug or newer
* **JDK:** Version 17 or 21+
* **Minimum SDK:** API 24 (Android 7.0 Nougat)
* **Target SDK:** API 34+

### Clone & Build

```bash
# 1. Clone the repository
git clone https://github.com/mdshohaibahammad/test.git

# 2. Open project directory
cd test

# 3. Build Debug APK using Gradle Wrapper
./gradlew assembleDebug
```

The compiled APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📜 License
This project is open-source and available under the [MIT License](LICENSE).
