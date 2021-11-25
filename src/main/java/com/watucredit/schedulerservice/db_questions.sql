/*Question One*/

/**Vehicles with unit sales above 1000 */
SELECT vm.name FROM `asset-schema`.`asset`a INNER JOIN `asset-schema`.`vehicle_model` vmo ON a.model_id = vmo.id
INNER JOIN `asset-schema`.vehicle_make vm ON vmo.vehicle_make_id = vm.id
INNER JOIN `loan-schema`.`m_loan` l ON l.id = a.m_loan_id
WHERE DATE(l.disbursedon_date) >= '2020-01-01' AND DATE(l.disbursedon_date) < '2020-03-01'
GROUP BY vm.name
HAVING COUNT(a.id) >= 1000


/**All vehicles including those wthout sales*/
SELECT vm.name AS make, vmo.name AS model, a.registration_no,a.m_loan_id,l.disbursedon_date FROM `asset-schema`.`vehicle_make` vm
INNER JOIN `asset-schema`.`vehicle_model` vmo ON vm.`id` = vmo.`vehicle_make_id`
LEFT JOIN `asset-schema`.`asset` a ON a.`model_id` = vmo.id
LEFT JOIN `loan-schema`.`m_loan` l ON l.id = a.`m_loan_id`
WHERE DATE(l.disbursedon_date) >= '2021-01-01' AND DATE(l.disbursedon_date) < '2020-03-01' OR l.`disbursedon_date` IS NULL


/*Question Two*/

SELECT loan_id, CAST(DATEDIFF(duedate, Date(Now()))/7 AS DECIMAL) AS NoOfWeeks, (principal_amount + interest_amount) / CAST(DATEDIFF(duedate, DATE(Now()))/7 AS DECIMAL)
AS WeeklyAmount FROM `loan-schema`.m_loan_repayment_schedule WHERE completed_derived = 0  ORDER BY id DESC limit 1


/*Question Three*/

WITH schedules AS(SELECT * FROM m_loan_repayment_schedule WHERE completed_derived = 0  ORDER BY id DESC LIMIT 1)
SELECT (IFNULL(s.principal_amount,0) + IFNULL(s.`interest_amount`,0) + IFNULL(s.`fee_charges_amount`,0) + IFNULL(s.`penalty_charges_amount`,0)) - IFNULL(sum(ml.amount),0) AS
           balance FROM schedules s LEFT JOIN m_loan_transaction ml ON s.loan_id = ml.loan_id
GROUP BY s.id