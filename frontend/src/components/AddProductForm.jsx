import { useState } from 'react';

const initialState = {
  name: '',
  description: '',
  price: '',
  file: null
};

export default function AddProductForm({ onCreate }) {
  const [form, setForm] = useState(initialState);

  function updateField(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function updateFile(event) {
    const file = event.target.files && event.target.files[0] ? event.target.files[0] : null;
    setForm((prev) => ({ ...prev, file }));
  }

  async function submit(event) {
    event.preventDefault();
    await onCreate({
      name: form.name,
      description: form.description,
      price: Number(form.price),
      file: form.file
    });
    setForm(initialState);
    event.target.reset();
  }

  return (
    <section className="panel">
      <div className="panel-header">
        <h2>Add Product</h2>
      </div>
      <form className="form-stack" onSubmit={submit}>
        <input name="name" value={form.name} onChange={updateField} placeholder="Name" required />
        <textarea
          name="description"
          value={form.description}
          onChange={updateField}
          placeholder="Description"
          required
        />
        <input name="file" onChange={updateFile} type="file" accept="image/*" required />
        <input
          name="price"
          value={form.price}
          onChange={updateField}
          placeholder="Price"
          min="0.01"
          step="0.01"
          type="number"
          required
        />
        <button type="submit">Upload & Create</button>
      </form>
    </section>
  );
}
