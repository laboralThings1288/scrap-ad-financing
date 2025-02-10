# Financing Flow Documentation

## 1. Financing Decision Flow

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Seller     │     │    Offer     │     │  Financing   │
│ Organization ├────►│   Creation   ├────►│   Request    │
└──────────────┘     └──────────────┘     └──────┬───────┘
                                                 │
                                                 ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Payment    │     │    Offer     │     │  Eligibility │
│   Process    │◄────│  Acceptance  │◄────│    Check     │
└──────────────┘     └──────────────┘     └──────────────┘
```

## 2. Eligibility Evaluation Process

```
┌─────────────────────────────────────────────────────┐
│              Organization Evaluation                 │
├─────────────┬─────────────────────────┬─────────────┤
│  Location   │     Ad Count Check      │    Age      │
│  Check      │                         │   Check     │
└──────┬──────┴──────────┬──────────────┴──────┬──────┘
       │                 │                      │
       ▼                 ▼                      ▼
┌──────────────┐  ┌──────────────┐     ┌──────────────┐
│ SPAIN/FRANCE │  │   >10,000    │     │   >1 Year    │
│    Check     │  │     Ads      │     │     Old      │
└──────┬───────┘  └──────┬───────┘     └──────┬───────┘
       │                 │                     │
       └─────────────────┴─────────────────────┘
                         │
                         ▼
                ┌─────────────────┐
                │   Financing     │
                │   Provider      │
                │   Selection     │
                └────────┬────────┘
                        │
            ┌──────────┴───────────┐
            ▼                      ▼
    ┌──────────────┐      ┌──────────────┐
    │     Bank     │      │   Fintech    │
    │    (5% fee)  │      │   (7% fee)   │
    └──────────────┘      └──────────────┘
```

## 3. Payment Flow

```
┌──────────────┐
│    Offer     │
│  Acceptance  │
└──────┬───────┘
       │
       ▼
┌──────────────┐     ┌──────────────┐
│  Calculate   │     │   Apply      │
│   Total     ├────►│  Financing   │
│   Amount    │     │     Fee      │
└──────────────┘     └──────┬───────┘
                           │
                           ▼
┌──────────────┐     ┌──────────────┐
│   Update     │     │  Process     │
│   Offer     │◄────│  Payment     │
│   Status    │     │              │
└──────────────┘     └──────────────┘
```

## 4. State Transitions

```
┌───────────┐     ┌───────────┐     ┌───────────┐
│  Created  │     │ Financing │     │ Accepted  │
│   Offer   ├────►│ Requested ├────►│  Offer    │
└───────────┘     └───────────┘     └───────────┘
     │                                    │
     │                                    │
     │            ┌───────────┐          │
     └───────────►│ Accepted  │◄─────────┘
                  │ Without   │
                  │ Financing │
                  └───────────┘
```

## 5. Fee Calculation Example

```
┌────────────────────────┐
│    Original Amount     │
│       100,000         │
└──────────┬────────────┘
           │
           ▼
┌────────────────────────┐
│    Bank Fee (5%)       │
│       5,000           │
└──────────┬────────────┘
           │
           ▼
┌────────────────────────┐
│    Final Amount        │
│       95,000          │
└────────────────────────┘
```

## Notes

1. **Eligibility Rules**
   - Location-based provider selection
   - Minimum ad count requirement
   - Organization age verification

2. **Fee Structure**
   - Bank: 5% fee for Spain/France
   - Fintech: 7% fee for other countries

3. **State Management**
   - Atomic transactions
   - Status tracking
   - Audit logging

4. **Validation Points**
   - Organization eligibility
   - Amount limits
   - Payment method compatibility 