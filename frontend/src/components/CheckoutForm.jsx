import { useState } from 'react';

const initialState = {
  fullName: '',
  phoneNumber: '',
  addressLine1: '',
  addressLine2: '',
  city: '',
  state: '',
  postalCode: '',
  country: '',
  paymentMethod: 'COD'
};

export default function CheckoutForm({ disabled, onCheckout }) {
  const [form, setForm] = useState(initialState);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  async function submit(event) {
    event.preventDefault();
    await onCheckout(form);
  }

  return (
    <section className="panel">
      <div className="panel-header">
        <h2>Delivery & Payment</h2>
      </div>
      <form className="form-stack" onSubmit={submit}>
        <input name="fullName" value={form.fullName} onChange={updateField} placeholder="Full Name" required />
        <input name="phoneNumber" value={form.phoneNumber} onChange={updateField} placeholder="Phone Number" required />
        <input name="addressLine1" value={form.addressLine1} onChange={updateField} placeholder="Address Line 1" required />
        <input name="addressLine2" value={form.addressLine2} onChange={updateField} placeholder="Address Line 2" />
        <input name="city" value={form.city} onChange={updateField} placeholder="City" required />
        <input name="state" value={form.state} onChange={updateField} placeholder="State" required />
        <input name="postalCode" value={form.postalCode} onChange={updateField} placeholder="Postal Code" required />
        <input name="country" value={form.country} onChange={updateField} placeholder="Country" required />
        <select name="paymentMethod" value={form.paymentMethod} onChange={updateField}>
          <option value="COD">Cash on Delivery (COD)</option>
        </select>
        <button className="checkout-btn" disabled={disabled} type="submit">
          Place Order (COD)
        </button>
      </form>
    </section>
  );
}
